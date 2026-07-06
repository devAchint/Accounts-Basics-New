package com.techuntried.accountsbasics2.ui.progress

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.CategoryProgressModel
import com.techuntried.accountsbasics2.domain.model.CategoryWithProgressModel
import com.techuntried.accountsbasics2.domain.model.category.CategoryModel
import com.techuntried.accountsbasics2.usecases.GetCategoriesUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ProgressSortOption(val title: String) {
    RECENTS("Recents"),
    ACCURACY("Accuracy"),
    COMPLETION("Completion")
}


sealed interface ProgressUiState {
    object Loading : ProgressUiState
    data class Error(val message: String?) : ProgressUiState
    data class Success(
        val progressList: List<CategoryWithProgressModel>,
        val questionsAttempted: Int = 0,
        val accuracy: Int = 0,
        val streak: Int = 0,
        val categoriesPlayed: Int = 0,
        val selectedSortOption: ProgressSortOption
    ) : ProgressUiState {
        fun isEmpty(): Boolean = progressList.isEmpty()
    }
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val logEventUseCase: LogEventUseCase
) :
    ViewModel() {

    private val _progressSort = MutableStateFlow(ProgressSortOption.RECENTS)

    private val categoryMapFlow: Flow<ApiResult<Map<Int, CategoryModel>>> = flow {
        when (val result = getCategoriesUseCase(null)) {
            is ApiResult.Success -> {
                val map = result.data.associateBy { it.categoryId }
                emit(ApiResult.Success(map))
            }

            is ApiResult.Error -> {
                emit(ApiResult.Error(result.errorMessage))
            }
        }
    }

    val progressUiState: StateFlow<ProgressUiState> =
        combine(
            roomRepository.observeUserStats(),
            categoryMapFlow,
            _progressSort
        ) { userStats, categoryResult, sortOption ->

            if (categoryResult is ApiResult.Error) {
                return@combine ProgressUiState.Error(categoryResult.errorMessage)
            }
            val categoryMap = (categoryResult as ApiResult.Success).data
            val progressList =
                userStats.filter { (it.levelsPlayed + it.correctAnswered + it.wrongAnswered) > 0 }
                    .mapNotNull { stat ->
                        val category = categoryMap[stat.categoryId] ?: return@mapNotNull null

                        val progress = if (category.levels > 0) {
                            (stat.levelsPlayed.toFloat() / category.levels) * 100
                        } else 0f

                        val totalAnswers = stat.correctAnswered + stat.wrongAnswered
                        val accuracy = if (totalAnswers > 0) {
                            (stat.correctAnswered.toFloat() / totalAnswers) * 100f
                        } else 0f

                        val categoryProgressModel = CategoryProgressModel(
                            categoryId = category.categoryId,
                            levelsPlayed = stat.levelsPlayed,
                            correctAnswered = stat.correctAnswered,
                            wrongAnswered = stat.wrongAnswered,
                            lastPlayedTime = stat.lastPlayedTime,
                            progressPercentage = progress,
                            accuracy = accuracy
                        )

                        CategoryWithProgressModel(
                            category = category,
                            progress = categoryProgressModel
                        )
                    }

            val sortedProgressList = when (sortOption) {
                ProgressSortOption.RECENTS ->
                    progressList.sortedByDescending { it.progress.lastPlayedTime }

                ProgressSortOption.ACCURACY ->
                    progressList
                        .sortedByDescending { it.progress.accuracy }

                ProgressSortOption.COMPLETION ->
                    progressList
                        .sortedByDescending { it.progress.progressPercentage }
            }

            val totalCorrect = sortedProgressList.sumOf { it.progress.correctAnswered }
            val totalWrong = sortedProgressList.sumOf { it.progress.wrongAnswered }
            val totalAttempted = totalCorrect + totalWrong

            val totalAccuracy = if (totalAttempted > 0) {
                (totalCorrect.toFloat() / totalAttempted) * 100f
            } else 0f

            ProgressUiState.Success(
                progressList = sortedProgressList,
                accuracy = totalAccuracy.toInt(),
                questionsAttempted = totalAttempted,
                categoriesPlayed = sortedProgressList.map { it.category.categoryId }
                    .distinct().size,
                selectedSortOption = sortOption
            )
        }
            .distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ProgressUiState.Loading
            )


    fun updateSort(sort: ProgressSortOption) {
        _progressSort.value = sort
    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }
}
   