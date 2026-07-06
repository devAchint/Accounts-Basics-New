package com.techuntried.accountsbasics2.ui.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.domain.model.category.CategoryModel
import com.techuntried.accountsbasics2.usecases.GetCategoriesUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadSuggestionWithLimitUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ExploreSectionModel(
    val title: String,
    val categories: List<CategoryModel>,
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase
) :
    ViewModel() {

    private val _exploreUiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val exploreUiState = _exploreUiState.asStateFlow()
    private var categories = emptyList<CategoryModel>()


    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _exploreUiState.value = ExploreUiState.Loading
            try {
                when (val response = getCategoriesUseCase(null)) {
                    is ApiResult.Error -> {
                        _exploreUiState.value =
                            ExploreUiState.Error(errorMessage = response.errorMessage)
                    }

                    is ApiResult.Success -> {
                        //change before publishing
                        val categories = response.data
//                            .filter { it.active }
                            .sortedWith(
                                compareBy<CategoryModel> { it.grade }
                                    .thenByDescending { it.weight }
                            )
                        val sections = categories.sortedByDescending { it.sectionWeight }
                            .mapNotNull { it.section }.distinct()
                        this@ExploreViewModel.categories = categories
                        val allSections = listOf("All") + sections

                        val exploreSections = filterCategories(section = "All", grades = emptySet())

                        _exploreUiState.update {
                            ExploreUiState.Success(
                                sections = allSections,
                                data = exploreSections,
                                selectedSection = "All",
                                selectedGrades = emptySet()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
                _exploreUiState.value =
                    ExploreUiState.Error(errorMessage = e.message?:"Unknown Error")
            }
        }
    }


    fun updateSection(section: String) {
        _exploreUiState.update {
            it.updateSuccess {
                val filteredData =
                    filterCategories(section = section, grades = selectedGrades)
                copy(
                    selectedSection = section,
                    data = filteredData
                )
            }
        }
    }

    fun updateGrade(grades: Set<Int>) {
        _exploreUiState.update {
            it.updateSuccess {
                val filteredData =
                    filterCategories(section = selectedSection, grades = grades)
                copy(
                    selectedGrades = grades,
                    data = filteredData
                )
            }
        }
    }

    fun filterCategories(section: String, grades: Set<Int>): List<ExploreSectionModel> {
        return categories
            .filter {
                val sectionCheck = if (section == "All") true else it.section == section
                val gradeCheck =
                    grades.isEmpty() || it.grade in grades
                sectionCheck && gradeCheck
            }
            .groupBy { it.section }
            .map {
                ExploreSectionModel(
                    title = it.key ?: "Others",
                    categories = it.value
                )
            }
    }

    fun uploadSuggestion(comment:String) {
        viewModelScope.launch {
            _exploreUiState.update { it.withActionLoading(true) }
            when (val result = uploadSuggestionWithLimitUseCase.invoke(comment)) {
                is ApiResult.Error -> {
                    _exploreUiState.update {
                        it.withActionLoading(false)
                            .withMessage(result.errorMessage)
                    }
                }


                is ApiResult.Success -> {
                    _exploreUiState.update {
                        it.withActionLoading(false)
                            .withMessage(result.data)
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
        _exploreUiState.update { it.withMessage(null) }
    }
}
   