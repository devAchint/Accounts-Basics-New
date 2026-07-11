package com.techuntried.accountsbasics2.ui.sectionCategories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.ui.navigation.Routes
import com.techuntried.accountsbasics2.usecases.GetSubjectsUseCase
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import com.techuntried.accountsbasics2.usecases.UploadSuggestionWithLimitUseCase
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class SectionCategoriesViewModel @Inject constructor(
    private val uploadSuggestionWithLimitUseCase: UploadSuggestionWithLimitUseCase,
    private val getSubjectsUseCase: GetSubjectsUseCase,
    private val logEventUseCase: LogEventUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val globalConfigController: GlobalConfigController
) :
    ViewModel() {

    private val _sectionCategoriesUiState =
        MutableStateFlow<SectionCategoriesUiState>(SectionCategoriesUiState.Loading)
    val sectionCategoriesUiState = _sectionCategoriesUiState.asStateFlow()

    val globalConfigState = globalConfigController.globalConfig

    val args = savedStateHandle.toRoute<Routes.SectionCategoriesScreenRoute>()


    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _sectionCategoriesUiState.update { SectionCategoriesUiState.Loading }
            try {
                when (val response =
                    getSubjectsUseCase(course = 1)) {
                    is ApiResult.Error -> {
                        _sectionCategoriesUiState.update {
                            SectionCategoriesUiState.Error(
                                errorMsg = response.errorMessage
                            )
                        }
                    }

                    is ApiResult.Success -> {
                        val allCategories = response.data
                            //  .filter { it.active } //remove before publishing
                            .filter { it.section == args.section }
                            .sortedWith(
                                compareBy<SubjectModel> { it.course }
                                    .thenByDescending { it.weight }
                            )

                        _sectionCategoriesUiState.update {
                            SectionCategoriesUiState.Success(categories = allCategories)
                        }
                    }
                }
            } catch (e: Exception) {
                _sectionCategoriesUiState.update {
                    SectionCategoriesUiState.Error(
                        errorMsg = e.message
                    )
                }
            }
        }
    }

    fun uploadSuggestion(comment: String) {
        viewModelScope.launch {
            _sectionCategoriesUiState.update { it.withActionLoading(true) }
            when (val result = uploadSuggestionWithLimitUseCase.invoke(comment)) {
                is ApiResult.Error -> {
                    _sectionCategoriesUiState.update {
                        it.withActionLoading(false)
                            .withMessage(result.errorMessage)
                    }
                }


                is ApiResult.Success -> {
                    _sectionCategoriesUiState.update {
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
        _sectionCategoriesUiState.update { it.withMessage(newMessage = null) }
    }


}
   