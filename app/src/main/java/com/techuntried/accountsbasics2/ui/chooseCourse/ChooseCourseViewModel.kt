package com.techuntried.accountsbasics2.ui.chooseCourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.domain.model.course.CourseResponse
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface CoursesListUiState{
    data object Loading: CoursesListUiState
    data class Success(val courses:List<CourseResponse>): CoursesListUiState
}

data class ChooseCourseUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val currentCourse: Int? = null,
    val courseSaved: Boolean = false
)

@HiltViewModel
class ChooseCourseViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val logEventUseCase: LogEventUseCase,
    private val networkRepository: NetworkRepository
) :
    ViewModel() {

    private val _coursesList = MutableStateFlow<CoursesListUiState>(
        CoursesListUiState.Loading)
    val coursesList = _coursesList.asStateFlow()

    private val _chooseCourseUiState = MutableStateFlow<ChooseCourseUiState>(ChooseCourseUiState())
    val chooseCourseUiState = _chooseCourseUiState.asStateFlow()

    init {
        fetchCourses()
        fetchUserCourse()
    }

    fun fetchCourses(){
        viewModelScope.launch {
            try {
                val response = networkRepository.fetchCourses()
                when(response){
                    is ApiResult.Error -> {

                    }
                    is ApiResult.Success -> {
                        if (response.data.status){
                            _coursesList.value = CoursesListUiState.Success(response.data.data)
                        }
                    }
                }
            }catch (e:Exception){

            }
        }
    }


    private fun fetchUserCourse() {
        dataStoreRepository.getUserCourseFlow()
            .onEach { courseNumber ->

                _chooseCourseUiState.update {
                    it.copy(currentCourse = courseNumber)
                }
            }
            .catch { e ->
                _chooseCourseUiState.update {
                    it.copy(message = e.message)
                }
            }
            .launchIn(viewModelScope)
    }


    fun saveUserCourse(course: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveUserCourse(course)
                _chooseCourseUiState.update { it.copy(courseSaved = true) }
            } catch (e: Exception) {
                _chooseCourseUiState.update { it.copy(message = e.message) }
            }
        }
    }

    fun clearNavigation() {
        _chooseCourseUiState.update { it.copy(courseSaved = false) }
    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

    fun clearMsg() {
        _chooseCourseUiState.update { it.copy(message = null) }
    }

}
