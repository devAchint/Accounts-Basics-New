package com.techuntried.accountsbasics2.ui.sectionCategories

import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel

sealed interface SectionCategoriesUiState {
    val message: String?
    val actionLoading: Boolean

    object Loading : SectionCategoriesUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Error(
        val errorMsg: String?,
        override val message: String? = null,
        override val actionLoading: Boolean = false
    ) : SectionCategoriesUiState

    data class Success(
        val categories: List<SubjectModel> = emptyList(),
        override val message: String? = null,
        override val actionLoading: Boolean = false
    ) : SectionCategoriesUiState {
        fun isEmpty(): Boolean = categories.isEmpty()
    }
}

fun SectionCategoriesUiState.withMessage(newMessage: String?): SectionCategoriesUiState {
    return when (this) {
        is SectionCategoriesUiState.Success -> this.copy(message = newMessage)
        is SectionCategoriesUiState.Error -> this.copy(message = newMessage)
        is SectionCategoriesUiState.Loading -> this
    }
}

fun SectionCategoriesUiState.withActionLoading(isLoading: Boolean): SectionCategoriesUiState {
    return when (this) {
        is SectionCategoriesUiState.Success -> this.copy(actionLoading = isLoading)
        is SectionCategoriesUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}