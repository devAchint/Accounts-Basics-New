package com.techuntried.accountsbasics2.ui.home

import com.techuntried.accountsbasics2.domain.model.SubjectWithProgressModel

sealed interface HomeUiState {
    val message: String?
    val actionLoading: Boolean

    object Loading: HomeUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Error(
        val errorMessage: String,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
    ) : HomeUiState

    data class Success(
        val sectionCategories: List<SectionSubjectsModel>? = null,
        val lastPlayedSubject: SubjectWithProgressModel? = null,
        val actionMessage: String? = null,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
    ) : HomeUiState
}

fun HomeUiState.withMessage(newMessage: String?): HomeUiState {
    return when (this) {
        is HomeUiState.Success -> this.copy(message = newMessage)
        is HomeUiState.Error -> this.copy(message = newMessage)
        is HomeUiState.Loading -> this
    }
}

fun HomeUiState.withActionLoading(isLoading: Boolean): HomeUiState {
    return when (this) {
        is HomeUiState.Success -> this.copy(actionLoading = isLoading)
        is HomeUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}

inline fun HomeUiState.updateSuccess(block: HomeUiState.Success.() -> HomeUiState): HomeUiState {
    return if (this is HomeUiState.Success) this.block() else this
}
