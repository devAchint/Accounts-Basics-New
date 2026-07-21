package com.techuntried.accountsbasics2.ui.improve

import com.techuntried.accountsbasics2.domain.model.entities.WrongQuestionEntity
import com.techuntried.accountsbasics2.ui.chapter.ChapterActions
import com.techuntried.accountsbasics2.usecases.LogEventType

sealed interface ImproveUiState {
    val message: String?
    val actionLoading: Boolean

    object Loading: ImproveUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Error(
        val errorMessage: String,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
    ) : ImproveUiState

    data class Success(
        val wrongQuestions: List<WrongQuestionEntity>,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
    ) : ImproveUiState
}

fun ImproveUiState.withMessage(newMessage: String?): ImproveUiState {
    return when (this) {
        is ImproveUiState.Success -> this.copy(message = newMessage)
        is ImproveUiState.Error -> this.copy(message = newMessage)
        is ImproveUiState.Loading -> this
    }
}

fun ImproveUiState.withActionLoading(isLoading: Boolean): ImproveUiState {
    return when (this) {
        is ImproveUiState.Success -> this.copy(actionLoading = isLoading)
        is ImproveUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}

inline fun ImproveUiState.updateSuccess(block: ImproveUiState.Success.() -> ImproveUiState): ImproveUiState {
    return if (this is ImproveUiState.Success) this.block() else this
}

sealed interface ImproveActions {
    data class AddCoin(val coins: Int) : ImproveActions
    data class UploadSuggestion(val comment: String) : ImproveActions
    data object Refresh : ImproveActions
    data class LogEvent(val logEventType: LogEventType): ImproveActions
}