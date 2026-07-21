package com.techuntried.accountsbasics2.ui.chapter

import com.techuntried.accountsbasics2.domain.model.level.ChapterModel

sealed interface ChapterUiState {
    val message: String?
    val actionLoading: Boolean

    data object Loading : ChapterUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Success(
        val chapters: List<ChapterModel>,
        val chaptersCompleted: Int,
        val unlockCoinsCost:Int,
        val chapterUnlocked: Int? = null,
        override val actionLoading: Boolean = false,
        override val message: String? = null,
    ) : ChapterUiState {
        fun isEmpty() = chapters.isEmpty()
    }

    data class Error(
        val errorMessage:String?,
        override val message: String?=null,
        override val actionLoading: Boolean = false,
    ) : ChapterUiState
}

fun ChapterUiState.withMessage(newMessage: String?): ChapterUiState {
    return when (this) {
        is ChapterUiState.Success -> this.copy(message = newMessage)
        is ChapterUiState.Error -> this.copy(message = newMessage)
        is ChapterUiState.Loading -> this
    }
}

fun ChapterUiState.withActionLoading(isLoading: Boolean): ChapterUiState {
    return when (this) {
        is ChapterUiState.Success -> this.copy(actionLoading = isLoading)
        is ChapterUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}


inline fun ChapterUiState.updateSuccess(block: ChapterUiState.Success.() -> ChapterUiState): ChapterUiState {
    return if (this is ChapterUiState.Success) this.block() else this
}

sealed interface ChapterActions {
    data class AddCoin(val coins: Int) : ChapterActions
    data class UploadSuggestion(val comment: String) : ChapterActions
    data class UnlockChapter(val chapterId: Int, val isAdWatched:Boolean) : ChapterActions
    data object ClearChapterUnlocked : ChapterActions
    data object Refresh : ChapterActions
}