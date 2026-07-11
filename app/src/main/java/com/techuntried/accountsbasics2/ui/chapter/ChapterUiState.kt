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
        val gameLevels: List<ChapterModel>,
        val levelsCompleted: Int,
        val unlockCoinsCost:Int,
        val levelUnlocked: Int? = null,
        override val actionLoading: Boolean = false,
        override val message: String? = null,
    ) : ChapterUiState {
        fun isEmpty() = gameLevels.isEmpty()
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

sealed interface LevelActions {
    data class AddCoin(val coins: Int) : LevelActions
    data class UploadSuggestion(val comment: String) : LevelActions
    data class UnlockLevel(val levelId: Int,val isAdWatched:Boolean) : LevelActions
    data object ClearLevelUnlocked : LevelActions
    data object Refresh : LevelActions
}