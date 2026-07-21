package com.techuntried.accountsbasics2.ui.improve

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.RoomRepository
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
    private val logEventUseCase: LogEventUseCase
) :
    ViewModel() {

    private val _improveUiState = MutableStateFlow<ImproveUiState>(ImproveUiState.Loading)
    val improveUiState = _improveUiState.asStateFlow()

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

    init {
        fetchData()
    }

    fun refreshData() {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _improveUiState.value = ImproveUiState.Loading
            try {
                when (val response = roomRepository.getWrongQuestions(null)) {
                    is ApiResult.Error -> {
                        Log.d("MYDEBUG", response.errorMessage)
                        _improveUiState.value = ImproveUiState.Error(
                            errorMessage = response.errorMessage
                        )
                    }

                    is ApiResult.Success -> {
                        _improveUiState.value = ImproveUiState.Success(
                            wrongQuestions = response.data
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
                _improveUiState.value = ImproveUiState.Error(
                    errorMessage = e.message ?: "Unknown Error"
                )
            }

        }
    }

    fun onAction(improveActions: ImproveActions) {
        when (improveActions) {
            is ImproveActions.AddCoin -> addCoins(improveActions.coins)
            is ImproveActions.UploadSuggestion -> uploadSuggestion(improveActions.comment)
            ImproveActions.Refresh -> refreshData()
            is ImproveActions.LogEvent -> logEvent(improveActions.logEventType)
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
                            .withMessage("Thanks! Your report has been recorded.")
                    }
                }
            }
        }

    }

    fun clearMsg() {
        _improveUiState.update { it.withMessage(newMessage = null) }
    }

}
   