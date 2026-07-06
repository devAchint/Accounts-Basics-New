package com.techuntried.accountsbasics2.ui.explore

sealed interface ExploreUiState {
    val message: String?
    val actionLoading: Boolean

    object Loading : ExploreUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Error(
        val errorMessage: String,
        override val message: String? = null,
        override val actionLoading: Boolean = false
    ) : ExploreUiState

    data class Success(
        val data: List<ExploreSectionModel> = emptyList(),
        val sections: List<String> = emptyList(),
        val selectedSection: String,
        val selectedGrades: Set<Int> ,
        override val message: String? = null,
        override val actionLoading: Boolean = false
    ) : ExploreUiState {
        fun isEmpty(): Boolean = data.isEmpty()
    }
}

fun ExploreUiState.withMessage(newMessage: String?): ExploreUiState {
    return when (this) {
        is ExploreUiState.Success -> this.copy(message = newMessage)
        is ExploreUiState.Error -> this.copy(message = newMessage)
        is ExploreUiState.Loading -> this
    }
}

fun ExploreUiState.withActionLoading(isLoading: Boolean): ExploreUiState {
    return when (this) {
        is ExploreUiState.Success -> this.copy(actionLoading = isLoading)
        is ExploreUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}


inline fun ExploreUiState.updateSuccess(block: ExploreUiState.Success.() -> ExploreUiState): ExploreUiState {
    return if (this is ExploreUiState.Success) this.block() else this
}
