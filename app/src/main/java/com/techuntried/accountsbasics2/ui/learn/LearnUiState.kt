package com.techuntried.accountsbasics2.ui.learn

import com.techuntried.accountsbasics2.domain.model.content.LearnContentModel
import com.techuntried.accountsbasics2.ui.chapter.ChapterUiState

sealed interface LearnUiState {
    val message: String?
    val actionLoading: Boolean

    data object Loading : LearnUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }
    data class Error(
        val errorMessage:String?,
        override val message: String?=null,
        override val actionLoading: Boolean = false,
    ) : LearnUiState

    data class Success(
        val content: List<LearnContentModel> ,
        override val message: String?=null,
        override val actionLoading: Boolean = false,
    ) : LearnUiState
}

fun LearnUiState.withMessage(newMessage: String?): LearnUiState {
    return when (this) {
        is LearnUiState.Success -> this.copy(message = newMessage)
        is LearnUiState.Error -> this.copy(message = newMessage)
        is LearnUiState.Loading -> this
    }
}

fun LearnUiState.withActionLoading(isLoading: Boolean): LearnUiState {
    return when (this) {
        is LearnUiState.Success -> this.copy(actionLoading = isLoading)
        is LearnUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}