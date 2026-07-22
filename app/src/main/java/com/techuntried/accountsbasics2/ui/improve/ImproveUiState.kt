package com.techuntried.accountsbasics2.ui.improve

import com.techuntried.accountsbasics2.usecases.LogEventType

data class MistakeItem(
    val id: Int,
    val subjectId: Int,
    val chapterId: Int,
    val questionId: Int,
    val subject: String,
    val date: String,
    val questionText: String,
    val yourAnswer: String?,
    val correctAnswer: String,
    val explanation: String,
    val isFixed: Boolean
)

data class SubjectChipData(
    val name: String,
    val count: Int
)

sealed interface ImproveUiState {
    val message: String?
    val actionLoading: Boolean

    object Loading : ImproveUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Error(
        val errorMessage: String,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
    ) : ImproveUiState

    data class Success(
        val mistakeItems: List<MistakeItem> = emptyList(),
        val toReviewCount: Int,
        val fixedThisWeekCount: Int,
        val subjectsAffectedCount: Int,
        val selectedSubject: String = "All Subjects",
        val subjects: List<SubjectChipData>,
        val activeExplanation: MistakeItem? = null,
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
        else -> this
    }
}

inline fun ImproveUiState.updateSuccess(block: ImproveUiState.Success.() -> ImproveUiState): ImproveUiState {
    return if (this is ImproveUiState.Success) this.block() else this
}

sealed interface ImproveActions {
    data class AddCoin(val coins: Int) : ImproveActions
    data class UploadSuggestion(val comment: String) : ImproveActions
    data object Refresh : ImproveActions
    data class LogEvent(val logEventType: LogEventType) : ImproveActions
    data class SelectSubject(val subject: String) : ImproveActions
    data class ShowExplanation(val item: MistakeItem?) : ImproveActions
}