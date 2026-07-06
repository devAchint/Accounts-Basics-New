package com.techuntried.accountsbasics2.ui.chooseGrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.utils.Grade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ChooseGradeUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val currentGrade: Grade? = null,
    val gradeSaved: Boolean = false
)

@HiltViewModel
class ChooseGradeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val logEventUseCase: LogEventUseCase
) :
    ViewModel() {

    private val _chooseGradeUiState = MutableStateFlow<ChooseGradeUiState>(ChooseGradeUiState())
    val chooseGradeUiState = _chooseGradeUiState.asStateFlow()

    init {
        fetchUserGrade()
    }

    private fun fetchUserGrade() {
        dataStoreRepository.getUserGradeFlow()
            .onEach { gradeNumber ->
                val grade = Grade.entries.firstOrNull { it.gradeNumber == gradeNumber }

                _chooseGradeUiState.update {
                    it.copy(currentGrade = grade)
                }
            }
            .catch { e ->
                _chooseGradeUiState.update {
                    it.copy(message = e.message)
                }
            }
            .launchIn(viewModelScope)
    }


    fun saveUserGrade(grade: Grade) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveUserGrade(grade.gradeNumber)
                _chooseGradeUiState.update { it.copy(gradeSaved = true) }
            } catch (e: Exception) {
                _chooseGradeUiState.update { it.copy(message = e.message) }
            }
        }
    }

    fun clearNavigation() {
        _chooseGradeUiState.update { it.copy(gradeSaved = false) }
    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

    fun clearMsg() {
        _chooseGradeUiState.update { it.copy(message = null) }
    }

}
