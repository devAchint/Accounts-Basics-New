package com.techuntried.accountsbasics2.ui.questions

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.mappers.asMistakeEntity
import com.techuntried.accountsbasics2.data.mappers.asQuestion
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.GameEconomy
import com.techuntried.accountsbasics2.domain.model.questions.QuestionModel
import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.usecases.GetQuestionsUseCase
import com.techuntried.accountsbasics2.usecases.GetSingleQuestionUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadFeedbackUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OptionType {
    Unselected, Selected, Disabled, Correct, Wrong
}


enum class AnswerState {
    Correct, Wrong, TimeUp
}

fun QuestionModel.shuffleOptions(): QuestionModel {
    val shuffledOptions = options.shuffled()
    return copy(
        options = shuffledOptions
    )
}

sealed interface QuestionEvent {

    data object StartOver : QuestionEvent
    data object SplitOptions : QuestionEvent
    data object AddTime : QuestionEvent
    data object Pause : QuestionEvent
    data object Resume : QuestionEvent
    data class CheckAnswer(val optionId: Int) : QuestionEvent
    data object NextQuestion : QuestionEvent
    data class TryAgain(val isAdWatched: Boolean) : QuestionEvent
    data class ToggleSound(val value: Boolean) : QuestionEvent
    data class ToggleShowCorrect(val value: Boolean) : QuestionEvent
    data class ToggleHaptic(val value: Boolean) : QuestionEvent
}

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreRepository: DataStoreRepository,
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val roomRepository: RoomRepository,
    private val globalConfigController: GlobalConfigController,
    private val uploadFeedbackUseCase: UploadFeedbackUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val getSingleQuestionUseCase: GetSingleQuestionUseCase
) :
    ViewModel() {

    private val _gameUiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val gameUiState = _gameUiState.asStateFlow()

    val coinsState: StateFlow<Int> = dataStoreRepository.fetchCoins()
        .catch { e ->
            Log.e("MYDEBUG", "Error fetching coins: ${e.message}")
            emit(0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val gameEconomy: StateFlow<GameEconomy> = dataStoreRepository.getGameEconomyFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameEconomy()
        )

    val globalConfigState = globalConfigController.globalConfig

    private var quizStartTimerJob: Job? = null
    private var questionTimerJob: Job? = null

    private var questionTimerCount: Int? = 15
    var pause: Int? = null

    private var gameQuestions = emptyList<QuestionModel>()
    private var isSoundEnabled = true
    private var isHapticEnabled = true
    private var isShowCorrectEnabled = true
    var questionReviewList = mutableListOf<QuestionReviewModel>()


    fun initialize(questionsTarget: QuestionsTarget) {
        fetchQuestions(questionsTarget)
        updateAnalytics(questionsTarget)
    }


    fun fetchQuestions(target: QuestionsTarget) {
        when (target) {
            QuestionsTarget.PracticeAllQuestions -> {
                questionTimerCount = null
                fetchPracticeAllQuestionsData(null)
            }

            is QuestionsTarget.PracticeQuestion -> {
                questionTimerCount = null
                fetchPracticeQuestionData(target.subjectId, target.chapterId, target.questionId)
            }

            is QuestionsTarget.Subject -> {
                questionTimerCount = target.timerCount
                fetchCategoryGameData(target.subjectId, target.chapterId)
            }
        }
    }

    fun onAction(action: QuestionEvent) {
        when (action) {
            QuestionEvent.AddTime -> useAddTimePowerUp()
            QuestionEvent.Pause -> pauseGame()
            QuestionEvent.Resume -> resumeGame()
            QuestionEvent.SplitOptions -> useSplitPowerUp()
            QuestionEvent.StartOver -> startOver()
            is QuestionEvent.CheckAnswer -> checkAnswer(action.optionId)
            QuestionEvent.NextQuestion -> nextQuestion()
            is QuestionEvent.ToggleHaptic -> toggleHaptic(action.value)
            is QuestionEvent.ToggleSound -> toggleSound(action.value)
            is QuestionEvent.ToggleShowCorrect -> toggleShowCorrect(action.value)
            is QuestionEvent.TryAgain -> tryAgain(action.isAdWatched)
        }
    }

    private fun fetchCategoryGameData(subjectId: Int, chapterId: Int) {
        viewModelScope.launch {
            try {
                val isSoundEnabled = dataStoreRepository.isSoundsEnabled()
                val isShowCorrectEnabled = dataStoreRepository.isShowCorrectEnabled()
                val isHapticEnabled = dataStoreRepository.isHapticEnabled()
                val response = getQuestionsUseCase(
                    subjectId,
                    chapterId
                )
                when (response) {
                    is ApiResult.Error -> {
                        _gameUiState.value = GameUiState.Error(response.errorMessage)
                    }

                    is ApiResult.Success -> {
                        val shuffledQuestions = response.data.shuffled()
                        if (shuffledQuestions.isNotEmpty()) {
                            gameQuestions = shuffledQuestions
                            this@QuestionsViewModel.isSoundEnabled = isSoundEnabled
                            this@QuestionsViewModel.isHapticEnabled = isHapticEnabled
                            this@QuestionsViewModel.isShowCorrectEnabled = isShowCorrectEnabled
                            startGame(3)
                        } else {
                            _gameUiState.value = GameUiState.Error("No Question Found")
                        }
                    }
                }

            } catch (e: Exception) {
                _gameUiState.value = GameUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private fun fetchPracticeAllQuestionsData(subjectId: Int?) {
        viewModelScope.launch {
            try {
                val isSoundEnabled = dataStoreRepository.isSoundsEnabled()
                val isShowCorrectEnabled = dataStoreRepository.isShowCorrectEnabled()
                val isHapticEnabled = dataStoreRepository.isHapticEnabled()
                val response = roomRepository.getMistakes(subjectId)
                when (response) {
                    is ApiResult.Error -> {
                        _gameUiState.value = GameUiState.Error(response.errorMessage)
                    }

                    is ApiResult.Success -> {
                        val shuffledQuestions = response.data.shuffled().map { it.asQuestion() }
                        if (shuffledQuestions.isNotEmpty()) {
                            gameQuestions = shuffledQuestions
                            this@QuestionsViewModel.isSoundEnabled = isSoundEnabled
                            this@QuestionsViewModel.isHapticEnabled = isHapticEnabled
                            this@QuestionsViewModel.isShowCorrectEnabled = isShowCorrectEnabled
                            startGame(3)
                        } else {
                            _gameUiState.value = GameUiState.Error("No Question Found")
                        }
                    }
                }

            } catch (e: Exception) {
                _gameUiState.value = GameUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private fun fetchPracticeQuestionData(subjectId: Int, chapterId: Int, questionId: Int) {
        viewModelScope.launch {
            try {
                val isSoundEnabled = dataStoreRepository.isSoundsEnabled()
                val isShowCorrectEnabled = dataStoreRepository.isShowCorrectEnabled()
                val isHapticEnabled = dataStoreRepository.isHapticEnabled()
                val response = getSingleQuestionUseCase(subjectId, chapterId, questionId)
                when (response) {
                    is ApiResult.Error -> {
                        _gameUiState.value = GameUiState.Error(response.errorMessage)
                    }

                    is ApiResult.Success -> {
                        val shuffledQuestions = response.data.shuffled()
                        if (shuffledQuestions.isNotEmpty()) {
                            gameQuestions = shuffledQuestions
                            this@QuestionsViewModel.isSoundEnabled = isSoundEnabled
                            this@QuestionsViewModel.isHapticEnabled = isHapticEnabled
                            this@QuestionsViewModel.isShowCorrectEnabled = isShowCorrectEnabled
                            startGame(3)
                        } else {
                            _gameUiState.value = GameUiState.Error("No Question Found")
                        }
                    }
                }

            } catch (e: Exception) {
                _gameUiState.value = GameUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private fun startGame(countdown: Int) {
        if (quizStartTimerJob == null) {
            _gameUiState.value =
                GameUiState.PreGameCountdown(countdown, isSoundEnabled = isSoundEnabled)
            quizStartTimerJob = viewModelScope.launch {
                var mQuizTimer = countdown
                while (mQuizTimer > 0) {
                    delay(1000)
                    mQuizTimer -= 1
                    _gameUiState.update { it.updatePreGameCountDown { copy(countdown = mQuizTimer) } }
                }
                setFirstQuestion()
                stopQuizTimer()
            }
        }
    }

    private fun setFirstQuestion() {
        _gameUiState.value =
            GameUiState.ActiveGame(
                currentQuestion = gameQuestions[0].shuffleOptions(),
                currentQuestionIndex = 0,
                totalQuestions = gameQuestions.size,
                isSoundEnabled = isSoundEnabled,
                isHapticEnabled = isHapticEnabled,
                isShowCorrectEnabled = isShowCorrectEnabled
            )
        resetQuestionTimer()
    }

    private fun resetQuestionTimer(extra: Int = 0) {
        pause?.let {
            pause = questionTimerCount
        } ?: run {
            stopQuestionTimer()
            startQuestionTimer(questionTimerCount?.plus(extra))
        }
    }

    private fun startQuestionTimer(timer: Int?) {
        if (timer != null) {
            questionTimerJob?.cancel()
            _gameUiState.update { it.updateActiveGame { copy(questionTimer = timer) } }
            questionTimerJob = viewModelScope.launch {
                var mQuestionTimer = timer
                while (mQuestionTimer > 0) {
                    delay(1000)
                    mQuestionTimer -= 1
                    _gameUiState.update { it.updateActiveGame { copy(questionTimer = mQuestionTimer) } }
                }

                //time up
                _gameUiState.update {
                    it.updateActiveGame {
                        copy(
                            answerState = AnswerState.TimeUp,
                            currentQuestion = currentQuestion.copy(
                                options = currentQuestion.options.map { option ->
                                    option.copy(
                                        optionType = OptionType.Disabled
                                    )
                                }
                            )
                        )
                    }
                }
                stopQuestionTimer()
            }

        }
    }

    private fun stopQuestionTimer() {
        questionTimerJob?.cancel()
        questionTimerJob = null
    }

    private fun stopQuizTimer() {
        quizStartTimerJob?.cancel()
        quizStartTimerJob = null
    }

    private fun resumeGame() {
        if (gameUiState.value is GameUiState.ActiveGame) {
            pause?.let {
                startQuestionTimer(it)
                pause = null
            }
        }

        if (gameUiState.value is GameUiState.PreGameCountdown) {
            pause?.let {
                startGame(it)
                pause = null
            }
        }
    }


    fun pauseGame() {
        val state = gameUiState.value
        if (state is GameUiState.ActiveGame) {
            if (state.answerState == null) {
                pause = state.questionTimer
                stopQuestionTimer()
            }
        }

        if (state is GameUiState.PreGameCountdown) {
            pause = state.countdown
            stopQuizTimer()
        }
    }

    fun checkAnswer(selectedOptionId: Int) {
        viewModelScope.launch {
            try {
                stopQuestionTimer()
                val activeState = _gameUiState.value as? GameUiState.ActiveGame ?: return@launch

                val isCorrect = selectedOptionId == activeState.currentQuestion.correctOptionId
                val selectedOption = activeState.currentQuestion.options.find {
                    it.optionId == selectedOptionId
                }

//                args.subjectId.let { id ->
                viewModelScope.launch {
                    if (isCorrect) {
                        roomRepository.updateCorrectAnswered(subjectId = activeState.currentQuestion.subjectId)
                        roomRepository.updateMistake(
                            activeState.currentQuestion.asMistakeEntity(
                                subjectId = activeState.currentQuestion.subjectId,
                                chapterId = activeState.currentQuestion.chapterId,
                                userAnswer = selectedOption?.optionText,
                                fixed = true
                            )
                        )
                    } else {
                        roomRepository.updateWrongAnswered(subjectId = activeState.currentQuestion.subjectId)
                        roomRepository.insertMistake(
                            activeState.currentQuestion.asMistakeEntity(
                                subjectId = activeState.currentQuestion.subjectId,
                                chapterId = activeState.currentQuestion.chapterId,
                                userAnswer = selectedOption?.optionText,
                                fixed = false
                            )
                        )
                    }
                }
//                }
                val correctOption = activeState.currentQuestion.options.find {
                    it.optionId == activeState.currentQuestion.correctOptionId
                }

                _gameUiState.update { state ->
                    state.updateActiveGame {
                        copy(
                            correctAnswers = if (isCorrect) correctAnswers + 1 else correctAnswers,
                            answerState = if (isCorrect) AnswerState.Correct else AnswerState.Wrong,
                            currentQuestionCorrectAnswer = correctOption?.optionText,
                            currentQuestion = currentQuestion.copy(
                                options = currentQuestion.options.map { option ->
                                    when (option.optionId) {
                                        selectedOptionId -> {
                                            option.copy(optionType = if (isCorrect) OptionType.Correct else OptionType.Wrong)
                                        }

                                        currentQuestion.correctOptionId -> {
                                            option.copy(optionType = OptionType.Correct)
                                        }

                                        else -> {
                                            option.copy(optionType = OptionType.Disabled)
                                        }
                                    }
                                }
                            )
                        )
                    }
                }

                if (correctOption != null && selectedOption != null) {
                    updateQuestionsReview(
                        currentQuestion = activeState.currentQuestion.questionText,
                        selectedAnswer = selectedOption.optionText,
                        correctAnswer = correctOption.optionText
                    )
                }

            } catch (e: Exception) {
                _gameUiState.update { it.withMessage(newMessage = e.message) }
            }
        }
    }

    fun tryAgain(isAdWatched: Boolean) {
        viewModelScope.launch {
            if (isAdWatched) {
                _gameUiState.update { state ->
                    state.updateActiveGame {
                        copy(
                            answerState = null,
                            currentQuestionCorrectAnswer = null,
                            hideWrongs = null,
                            currentQuestion = currentQuestion.copy(
                                options = currentQuestion.options.map {
                                    it.copy(
                                        optionType = OptionType.Unselected,
                                    )
                                }
                            )
                        )
                    }
                }
                resetQuestionTimer(extra = 5)
            } else {
                val tryAgainCost = gameEconomy.value.tryAgainCoins

                if (tryAgainCost <= coinsState.value) {
                    _gameUiState.update { state ->
                        state.updateActiveGame {
                            copy(
                                answerState = null,
                                currentQuestionCorrectAnswer = null,
                                hideWrongs = null,
                                currentQuestion = currentQuestion.copy(
                                    options = currentQuestion.options.map {
                                        it.copy(
                                            optionType = OptionType.Unselected,
                                        )
                                    }
                                )
                            )
                        }
                    }
                    dataStoreRepository.useCoins(tryAgainCost)
                    resetQuestionTimer()
                } else {
                    _gameUiState.update { it.withMessage(newMessage = "Insufficient coins") }
                }
            }
        }
    }


    fun nextQuestion() {
        val activeState = _gameUiState.value as? GameUiState.ActiveGame ?: return

        val currentIndex = activeState.currentQuestionIndex
        if (currentIndex + 1 < gameQuestions.size) {
            _gameUiState.update { state ->
                state.updateActiveGame {
                    copy(
                        currentQuestionIndex = currentIndex + 1,
                        currentQuestion = gameQuestions[currentIndex + 1].shuffleOptions(),
                        answerState = null,
                        currentQuestionCorrectAnswer = null,
                        hideWrongs = null
                    )
                }
            }
            resetQuestionTimer()
        } else {
            _gameUiState.update { state ->
                state.updateActiveGame {
                    copy(
                        isGameCompleted = true,
                        answerState = null,
                        currentQuestionCorrectAnswer = null,
                        hideWrongs = null
                    )
                }
            }
        }
    }

    private fun updateQuestionsReview(
        currentQuestion: String,
        selectedAnswer: String,
        correctAnswer: String
    ) {
        val questionReview = QuestionReviewModel(currentQuestion, selectedAnswer, correctAnswer)
        val currentList = questionReviewList
        val alreadyExistIndex =
            currentList.indexOfFirst { it.question == currentQuestion }
        if (alreadyExistIndex != -1) {
            val reviewModel = currentList.getOrNull(alreadyExistIndex)
            reviewModel?.let { review ->
                currentList.remove(review)
            }
        }
        currentList.add(questionReview)
    }

    private fun startOver() {
        stopQuestionTimer()
        stopQuizTimer()
        pause = null
        questionReviewList.clear()
        startGame(3)
    }

    private fun toggleSound(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setSoundEnabled(value)
            this@QuestionsViewModel.isSoundEnabled = value
            _gameUiState.update { it.updateActiveGame { copy(isSoundEnabled = value) } }
        }
    }

    private fun toggleShowCorrect(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setShowCorrect(value)
            this@QuestionsViewModel.isShowCorrectEnabled = value
            _gameUiState.update { it.updateActiveGame { copy(isShowCorrectEnabled = value) } }
        }
    }

    private fun toggleHaptic(value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setHapticEnabled(value)
            this@QuestionsViewModel.isHapticEnabled = value
            _gameUiState.update { it.updateActiveGame { copy(isHapticEnabled = value) } }
        }
    }

    fun useSplitPowerUp() {
        viewModelScope.launch {
            val bombCost = gameEconomy.value.bombCoins
            if (bombCost <= coinsState.value) {
                _gameUiState.update { state ->
                    state.updateActiveGame {
                        val wrongOptions = currentQuestion.options
                            .filter { option -> option.optionId != currentQuestion.correctOptionId }
                            .take(2)

                        copy(
                            hideWrongs = wrongOptions
                        )
                    }
                }
                dataStoreRepository.useCoins(bombCost)
            } else {
                _gameUiState.update { it.withMessage(newMessage = "Insufficient coins") }
            }
        }
    }

    fun useAddTimePowerUp() {
        viewModelScope.launch {
            val addTimeCost = gameEconomy.value.addTimeCoins
            if (addTimeCost <= coinsState.value) {
                val activeState = _gameUiState.value as? GameUiState.ActiveGame ?: return@launch
                val currentQuestionTime = activeState.questionTimer ?: return@launch
                val newTime = currentQuestionTime + 15
                stopQuestionTimer()
                startQuestionTimer(newTime)
                dataStoreRepository.useCoins(addTimeCost)
            } else {
                _gameUiState.update { it.withMessage(newMessage = "Insufficient coins") }
            }
        }
    }

    fun uploadReport(reason: String, details: String?) {
        viewModelScope.launch {
            _gameUiState.update { it.withActionLoading(true) }
            val state = gameUiState.value as? GameUiState.ActiveGame ?: return@launch
            val currentQuestion = state.currentQuestion
            val comment =
                "CategoryId ${currentQuestion.subjectId}\nLevelId ${currentQuestion.chapterId}\nQuestionId ${currentQuestion.questionText}\nReport :$reason\nDetails: $details"

            when (val response = uploadFeedbackUseCase(comment = comment)) {
                is ApiResult.Error -> {
                    //  _actionMsg.value = ApiResult.Error(response.errorMessage)
                }

                is ApiResult.Success -> {
                    _gameUiState.update {
                        it.withActionLoading(false)
                            .withMessage(newMessage = "Your Feedback has been recorded")
                    }
                }
            }

        }
    }


    fun updateAnalytics(target: QuestionsTarget) {
        when (target) {
            QuestionsTarget.PracticeAllQuestions -> {}
            is QuestionsTarget.PracticeQuestion -> {}
            is QuestionsTarget.Subject -> {
                logEvent(
                    LogEventType.PracticeChapterStart(
                        subjectId = target.subjectId,
                        chapterId = target.chapterId
                    )
                )
            }
        }

    }


    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

    fun clearMsg() {
        _gameUiState.update { it.withMessage(null) }
    }
}
