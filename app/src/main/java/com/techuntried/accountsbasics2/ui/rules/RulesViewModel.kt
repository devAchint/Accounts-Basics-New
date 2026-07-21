package com.techuntried.accountsbasics2.ui.rules

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.ui.navigation.Routes
import com.techuntried.accountsbasics2.usecases.GetSubjectDetailsUseCase
import com.techuntried.accountsbasics2.usecases.GetChapterDetailsUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RuleModel(
    val icon: Int,
    val title: String,
    val description: String
)

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val getChapterDetailsUseCase: GetChapterDetailsUseCase,
    private val getSubjectDetailsUseCase: GetSubjectDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreRepository: DataStoreRepository
) :
    ViewModel() {

    private val _rulesScreenUiState =
        MutableStateFlow<RulesScreenUiState>(RulesScreenUiState.Loading)
    val rulesScreenUiState = _rulesScreenUiState.asStateFlow()


    val args = savedStateHandle.toRoute<Routes.RulesScreenRoute>()


    init {
        prepareCategoryRules(categoryId = args.subjectId, levelId = args.chapterId)
    }

    fun refresh() {
        prepareCategoryRules(categoryId = args.subjectId, levelId = args.chapterId)
    }

    fun updateIsRuleFirstTime() {
        viewModelScope.launch {
            dataStoreRepository.setRuleFirstTime(false)
            _rulesScreenUiState.update { it.updateSuccess { copy(isRuleFirstTime = false) } }
        }
    }

    fun updateTimer(value: Boolean) {
        _rulesScreenUiState.update { currentState ->
            currentState.updateSuccess { copy(timerCount = if (value) 15 else null) }
        }
    }


    private fun prepareCategoryRules(
        categoryId: Int, levelId: Int
    ) {
        viewModelScope.launch {
            try {
                val isTimer = dataStoreRepository.isTimerEnabled()
                val isFirstTime = dataStoreRepository.isRuleFirstTime()

                _rulesScreenUiState.value = RulesScreenUiState.Loading

                val subjectResult = getSubjectDetailsUseCase(categoryId)
                val chapterResult = getChapterDetailsUseCase(categoryId, levelId)

                if (subjectResult is ApiResult.Success && chapterResult is ApiResult.Success) {
                    val totalQuestions = 0 //chapterResult.data.questions
                    val minimumScore = (60 * totalQuestions) / 100

                    val ruleList = listOf(
                        RuleModel(
                            icon = R.drawable.timer_icon,
                            title = "",
                            description = "Answer at least $minimumScore questions to upgrade to the next level."
                        ),
                        RuleModel(
                            icon = R.drawable.timer_icon,
                            title = "Time Limit",
                            description = "You have 15 seconds to answer each question. If you don’t answer in time, it will be marked incorrect."
                        ),
                        RuleModel(
                            icon = R.drawable.question_variety_icon,
                            title = "Question Variety",
                            description = "A diverse set of questions to test your knowledge."
                        ),
                        RuleModel(
                            icon = R.drawable.trophy_icon,
                            title = "Winning Criteria",
                            description = "To win the quiz, you must answer at least $minimumScore questions correctly."
                        ),
                        RuleModel(
                            icon = R.drawable.check_circle_icon,
                            title = "Quiz Completion",
                            description = "The quiz will automatically end after all $totalQuestions questions have been attempted."
                        ),
                    )
                    _rulesScreenUiState.value = RulesScreenUiState.Success(
                        rules = ruleList,
                        title = subjectResult.data.name,
                        iconUrl = subjectResult.data.imageUrl,
                        bgColor = subjectResult.data.bgColor,
                        timerCount = if (isTimer) 15 else null,
                        isRuleFirstTime = isFirstTime
                    )
                } else {
                    val errorMsg = when {
                        subjectResult is ApiResult.Error -> subjectResult.errorMessage
                        chapterResult is ApiResult.Error -> chapterResult.errorMessage
                        else -> "An unknown error occurred"
                    }

                    Log.d("MYDEBUG", errorMsg)
                    _rulesScreenUiState.update {
                        RulesScreenUiState.Error("Something went wrong")
                    }
                }
            } catch (e: Exception) {
                _rulesScreenUiState.update {
                    RulesScreenUiState.Error(e.message)
                }
            }

        }
    }

}