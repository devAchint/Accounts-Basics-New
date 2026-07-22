package com.techuntried.accountsbasics2.ui.improve

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.mappers.asMistakeItem
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.usecases.GetSubjectsUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadSuggestionWithLimitUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImproveViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val getSubjectsUseCase: GetSubjectsUseCase
) : ViewModel() {

    private val _improveUiState = MutableStateFlow<ImproveUiState>(ImproveUiState.Loading)
    val improveUiState = _improveUiState.asStateFlow()

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

    init {
        fetchData()
    }

    fun refreshData() {
        fetchData()
    }

    private fun fetchData() {
        try {
            viewModelScope.launch {
                val subjectMap = when (val response = getSubjectsUseCase(null)) {
                    is ApiResult.Success -> response.data.associate {
                        it.subjectId to it.name
                    }
                    is ApiResult.Error -> emptyMap()
                }

                roomRepository.getMistakes(null)
                    .collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                val items = result.data.mapIndexed { index, entity ->
                                    entity.asMistakeItem(
                                        id = index,
                                        subject = subjectMap[entity.subjectId] ?: "Unknown"
                                    )
                                }

                                _improveUiState.value = createSuccessState(items)
                            }

                            is ApiResult.Error -> {
                                _improveUiState.value =
                                    ImproveUiState.Error(result.errorMessage)
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            _improveUiState.value = ImproveUiState.Error(e.message ?: "Unknown Error")
        }
    }

    private fun createSuccessState(
        items: List<MistakeItem>,
    ): ImproveUiState.Success {
        val totalCount = items.size
        val uniqueSubjectsCount = items.map { it.subject }.distinct().size

        // Build dynamic subject chips
        val subjectCounts = items.groupBy { it.subject }.mapValues { it.value.size }
        val fixedCount = items.count { it.isFixed }
        val chipList = mutableListOf(SubjectChipData("All Subjects", totalCount))
        subjectCounts.forEach { (subjectName, count) ->
            chipList.add(SubjectChipData(subjectName, count))
        }

        return ImproveUiState.Success(
            mistakeItems = items,
            toReviewCount = totalCount,
            fixedThisWeekCount = fixedCount,
            subjectsAffectedCount = uniqueSubjectsCount,
            subjects = chipList,
            selectedSubject = "All Subjects"
        )
    }

    fun onAction(action: ImproveActions) {
        when (action) {
            is ImproveActions.AddCoin -> addCoins(action.coins)
            is ImproveActions.UploadSuggestion -> uploadSuggestion(action.comment)
            ImproveActions.Refresh -> refreshData()
            is ImproveActions.LogEvent -> logEvent(action.logEventType)
            is ImproveActions.SelectSubject -> selectSubject(action.subject)
            is ImproveActions.ShowExplanation -> showExplanation(action.item)
        }
    }

    private fun selectSubject(subject: String) {
        _improveUiState.update { state ->
            state.updateSuccess {
                copy(selectedSubject = subject)
            }
        }
    }

    private fun showExplanation(item: MistakeItem?) {
        _improveUiState.update { state ->
            state.updateSuccess {
                copy(activeExplanation = item)
            }
        }
    }


    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

    private fun addCoins(rewardAmount: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.addCoins(rewardAmount)
                _improveUiState.update { it.withMessage("Earned $rewardAmount coins") }
            } catch (e: Exception) {
                _improveUiState.update { it.withMessage(e.message) }
            }
        }
    }

    private fun uploadSuggestion(comment: String) {
        viewModelScope.launch {
            _improveUiState.update { it.withActionLoading(true) }
            when (val result = uploadSuggestionWithLimitUseCase.invoke(comment)) {
                is ApiResult.Error -> {
                    _improveUiState.update {
                        it.withActionLoading(false).withMessage(result.errorMessage)
                    }
                }

                is ApiResult.Success -> {
                    _improveUiState.update {
                        it.withActionLoading(false)
                            .withMessage("Thanks! Your suggestion has been received.")
                    }
                }
            }
        }
    }

    fun clearMsg() {
        _improveUiState.update { it.withMessage(newMessage = null) }
    }
}