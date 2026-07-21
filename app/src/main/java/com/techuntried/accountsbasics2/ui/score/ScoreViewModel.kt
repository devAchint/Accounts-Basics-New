package com.techuntried.accountsbasics2.ui.score


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.ui.navigation.Routes
import com.techuntried.accountsbasics2.ui.navigation.serializableType
import com.techuntried.accountsbasics2.usecases.GetSubjectDetailsUseCase
import com.techuntried.accountsbasics2.usecases.GetChapterDetailsUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadFeedbackUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import com.techuntried.accountsbasics2.utils.subscribeToTopic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import kotlin.reflect.typeOf


data class ScoreModel(
    val correct: Int,
    val totalQuestions: Int,
    val title: String,
    val description: String,
    val isWon: Boolean,
    val isLastLevel: Boolean,
    val coinsEarned: Int,
    val accuracy: Int
)

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    savedStateHandle: SavedStateHandle,
    private val logEventUseCase: LogEventUseCase,
    private val getChapterDetailsUseCase: GetChapterDetailsUseCase,
    private val getSubjectDetailsUseCase: GetSubjectDetailsUseCase,
    private val uploadFeedbackUseCase: UploadFeedbackUseCase,
    private val globalConfigController: GlobalConfigController,
    private val dataStoreRepository: DataStoreRepository
) :
    ViewModel() {

    private val scoreArgs = savedStateHandle.toRoute<Routes.ScoreScreenRoute>(
        mapOf(
            typeOf<List<QuestionReviewModel>>() to serializableType<List<QuestionReviewModel>>()
        )
    )

    private var correctAnswered = scoreArgs.correctAnswers
    private var subjectId = scoreArgs.subjectId
    private var chapterId = scoreArgs.chapterId
    private val totalQuestions = scoreArgs.totalQuestions
    private var review = scoreArgs.questionReview

    private val _scoreScreenUiState =
        MutableStateFlow<ScoreScreenUiState>(ScoreScreenUiState.Loading)
    val scoreScreenUiState = _scoreScreenUiState.asStateFlow()

    val coinsState: StateFlow<Int> = dataStoreRepository.fetchCoins()
        .catch { e ->
            Log.e("MYDEBUG", "Error fetching coins: ${e.message}")
            emit(0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Stops collecting 5s after UI leaves
            initialValue = 0
        )

    val config = globalConfigController.globalConfig

    init {
        fetchData()
        updateAnalytics(
            subjectId = subjectId,
            chapterId = chapterId,
            totalQuestions = totalQuestions,
            answeredCount = review.size
        )
    }


    fun fetchData() {
        calculateResult(correctAnswered, subjectId, chapterId,review)
    }

    fun refreshData() {
        fetchData()
    }


    private fun calculateResult(
        correctAnswers: Int,
        subjectId: Int,
        chapterId: Int,
        review: List<QuestionReviewModel>
    ) {
        viewModelScope.launch {
            _scoreScreenUiState.value = ScoreScreenUiState.Loading
            try {
                val subjectDeferred = async { getSubjectDetailsUseCase(subjectId) }
                val chapterDeferred = async { getChapterDetailsUseCase(subjectId, chapterId) }

                val subjectResult = subjectDeferred.await()
                val chapterResult = chapterDeferred.await()

                if (subjectResult is ApiResult.Success && chapterResult is ApiResult.Success) {
                    val rewardCoins = dataStoreRepository.fetchCorrectAnswerCoins()
                    val chapter = chapterResult.data
                    val subjectName = subjectResult.data.name
                    val section = subjectResult.data.section
                    val totalLevels = subjectResult.data.chapters

                    val totalQuestions = 0 //chapter.questions
                    val chapterName = chapter.name

                    // 🔹 BUSINESS LOGIC
                    val minimumScore = (totalQuestions * 60) / 100
                    val isWon = correctAnswers >= minimumScore
                    val coinsEarned = if (isWon) correctAnswers * rewardCoins else 0

                    val attempted = review.size
                    val accuracy =
                        if (attempted > 0)
                            ((correctAnswers.toDouble() / attempted) * 100).toInt()
                        else 0

                    val score = ScoreModel(
                        title = subjectName,
                        isWon = isWon,
                        isLastLevel = totalLevels == chapterId,
                        description = chapterName,
                        totalQuestions = totalQuestions,
                        correct = correctAnswers,
                        coinsEarned = coinsEarned,
                        accuracy = accuracy
                    )

                    // 🔹 UPDATE UI ONCE
                    _scoreScreenUiState.value = ScoreScreenUiState.Success(
                        score = score,
                        review = review,
                        levelRatingEnabled = config.value.levelRatingEnabled
                    )

                    // 🔹 SIDE EFFECTS (can be sequential or parallel)
                    handlePostScoreActions(
                        isWon = isWon,
                        coinsEarned = coinsEarned,
                        subjectId = subjectId,
                        chapterId = chapterId,
                        section = section,
                    )
                } else {
                    _scoreScreenUiState.value =
                        ScoreScreenUiState.Error(errorMessage = "Something went wrong")
                }
            } catch (e: Exception) {
                _scoreScreenUiState.value =
                    ScoreScreenUiState.Error(errorMessage = e.message ?: "Something went wrong")
            }
        }
    }

    private suspend fun handlePostScoreActions(
        isWon: Boolean,
        coinsEarned: Int,
        subjectId: Int,
        chapterId: Int,
        section: String?,
    ) {
        supervisorScope {
            if (isWon) {
                launch {
                    roomRepository.updateLevelsCompleted(subjectId, chapterId)
                }
            }
            launch {
                checkScoreRatingEnabled()
            }

            if (coinsEarned > 0) {
                launch {
                    internalAddCoins(coinsEarned)
                }
            }
        }
        section?.let {
            subscribeToTopic(section)
        }
    }



    fun updateAnalytics(
        subjectId: Int,
        chapterId: Int,
        answeredCount: Int,
        totalQuestions: Int,
    ) {
        logEvent(
            LogEventType.PracticeChapterComplete(
                subjectId = subjectId,
                chapterId = chapterId,
                answeredCount = answeredCount,
                totalQuestions = totalQuestions
            )
        )
    }

    private suspend fun checkScoreRatingEnabled() {
        try {
            if (config.value.scoreRatingEnabled) {
                val isScoreRatingDismissed = dataStoreRepository.isScoreRatingDismissed()
                val enoughLevelsPlayed =
                    (roomRepository.observeUserStats().firstOrNull()
                        ?.sumOf { it.chaptersCompleted }
                        ?: 0) > 5
                _scoreScreenUiState.update {
                    it.updateSuccess { copy(scoreRatingEnabled = !isScoreRatingDismissed && enoughLevelsPlayed ) }
                }
            }
        } catch (e: Exception) {
            Log.d("MYDEBUG", "Score Rating ${e.message}")
        }
    }

    fun onAction(scoreAction: ScoreActions) {
        when (scoreAction) {
            ScoreActions.DismissLevelRating -> dismissLevelRating()
            ScoreActions.DismissScoreRating -> dismissScoreRating()
            is ScoreActions.Refresh -> refreshData()
            is ScoreActions.AddCoins -> addCoins(scoreAction.coins)
            is ScoreActions.SubmitLevelRating -> submitFeedback(scoreAction.ratingText)
            is ScoreActions.SubmitAppRating -> submitFeedback(scoreAction.ratingText)
        }
    }

    private suspend fun internalAddCoins(rewardAmount: Int) {
        dataStoreRepository.addCoins(rewardAmount)
        _scoreScreenUiState.update { it.withMessage("Earned $rewardAmount coins") }
    }

    fun addCoins(coins: Int) {
        viewModelScope.launch {
            internalAddCoins(coins)
        }
    }

    private fun dismissScoreRating() {
        viewModelScope.launch {
            dataStoreRepository.setScoreRatingDismissed(true)
            _scoreScreenUiState.update { it.updateSuccess { copy(scoreRatingEnabled = false) } }
        }
    }

    private fun dismissLevelRating() {
        viewModelScope.launch {
            _scoreScreenUiState.update { it.updateSuccess { copy(levelRatingEnabled = false) } }
        }
    }

    private fun submitFeedback(comment: String) {
        _scoreScreenUiState.update { it.withActionLoading(true) }
        viewModelScope.launch {
            when (uploadFeedbackUseCase(comment)) {
                is ApiResult.Error -> {
                    _scoreScreenUiState.update {
                        it.withActionLoading(false)
                    }
                }

                is ApiResult.Success -> {
                    _scoreScreenUiState.update {
                        it.withMessage("Thanks for your feedback!")
                            .withActionLoading(false)
                            .updateSuccess { copy(levelRatingEnabled = false) }
                    }
                }
            }
        }
    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

    fun clearMsg() {
        _scoreScreenUiState.update { it.withMessage(null) }
    }
}