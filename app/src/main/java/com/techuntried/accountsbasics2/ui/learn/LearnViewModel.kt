package com.techuntried.accountsbasics2.ui.learn

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.ui.chapter.ChapterUiState
import com.techuntried.accountsbasics2.usecases.GetLearnContentUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class LearnViewModel @Inject constructor(
    private val getLearnContentUseCase: GetLearnContentUseCase
) :
    ViewModel() {

    private val _learnUiState = MutableStateFlow<LearnUiState>(LearnUiState.Loading)
    val learnUiState = _learnUiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        fetchContent()
    }

    private fun fetchContent() {
        viewModelScope.launch {
            _learnUiState.value = LearnUiState.Loading
            try {
                when (val response = getLearnContentUseCase(1, 1)) {
                    is ApiResult.Error -> {
                        Log.d("MYDEBUG", response.errorMessage)
                        _learnUiState.value = LearnUiState.Error(
                            errorMessage = response.errorMessage
                        )
                    }

                    is ApiResult.Success -> {
                        _learnUiState.value = LearnUiState.Success(
                            content = response.data,
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
                _learnUiState.value = LearnUiState.Error(
                    errorMessage = e.message
                )
            }

        }
    }

    fun clearMsg() {
        _learnUiState.update { it.withMessage(null) }
    }

}
   