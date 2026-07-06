package com.techuntried.accountsbasics2.ui.feedback

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.usecases.UploadSuggestionWithLimitUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class FeedbackUiState(
    val isLoading: Boolean = false,
    val feedback: String = "",
    val rating: Int = 0,
    val isRatingValid: Boolean? = null,
    val isFeedbackValid: Boolean? = null,
    val message: String? = null,
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase
) :
    ViewModel() {

    private val _feedbackUiState = MutableStateFlow<FeedbackUiState>(FeedbackUiState())
    val feedbackUiState = _feedbackUiState.asStateFlow()


    fun updateRating(rating: Int) {
        _feedbackUiState.update { it.copy(rating = rating) }
    }

    fun updateFeedback(feedback: String) {
        _feedbackUiState.update { it.copy(feedback = feedback) }
    }


    fun clearMsg() {
        _feedbackUiState.update { it.copy(message = null) }
    }


    fun submitFeedback() {
        viewModelScope.launch {
            try {
                _feedbackUiState.update {
                    it.copy(
                        isRatingValid = it.rating > 0f,
                        isFeedbackValid = it.feedback.length >= 10
                    )
                }

                if (_feedbackUiState.value.isRatingValid == true
                    && _feedbackUiState.value.isFeedbackValid == true
                ) {
                    _feedbackUiState.update { it.copy(isLoading = true) }
                    val state = feedbackUiState.value
                    val comment = "Rating ${state.rating}\nFeedback ${state.feedback}"
                    when (val response = uploadSuggestionWithLimitUseCase(comment)) {
                        is ApiResult.Error -> {
                            _feedbackUiState.update {
                                it.copy(
                                    isLoading = false,
                                    message = response.errorMessage
                                )
                            }
                        }

                        is ApiResult.Success -> {
                            _feedbackUiState.update {
                                it.copy(
                                    isLoading = false,
                                    message = response.data
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _feedbackUiState.update { it.copy(isLoading = false, message = e.message) }
            }
        }
    }
}
