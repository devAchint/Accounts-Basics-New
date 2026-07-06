package com.techuntried.accountsbasics2.ui.level

import com.techuntried.accountsbasics2.domain.model.level.LevelModel

sealed interface LevelUiState {
    val message: String?
    val actionLoading: Boolean

    data object Loading : LevelUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }

    data class Success(
        val gameLevels: List<LevelModel>,
        val levelsCompleted: Int,
        val unlockCoinsCost:Int,
        val levelUnlocked: Int? = null,
        override val actionLoading: Boolean = false,
        override val message: String? = null,
    ) : LevelUiState {
        fun isEmpty() = gameLevels.isEmpty()
    }

    data class Error(
        val errorMessage:String?,
        override val message: String?=null,
        override val actionLoading: Boolean = false,
    ) : LevelUiState
}

fun LevelUiState.withMessage(newMessage: String?): LevelUiState {
    return when (this) {
        is LevelUiState.Success -> this.copy(message = newMessage)
        is LevelUiState.Error -> this.copy(message = newMessage)
        is LevelUiState.Loading -> this
    }
}

fun LevelUiState.withActionLoading(isLoading: Boolean): LevelUiState {
    return when (this) {
        is LevelUiState.Success -> this.copy(actionLoading = isLoading)
        is LevelUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}


inline fun LevelUiState.updateSuccess(block: LevelUiState.Success.() -> LevelUiState): LevelUiState {
    return if (this is LevelUiState.Success) this.block() else this
}

sealed interface LevelActions {
    data class AddCoin(val coins: Int) : LevelActions
    data class UploadSuggestion(val comment: String) : LevelActions
    data class UnlockLevel(val levelId: Int,val isAdWatched:Boolean) : LevelActions
    data object ClearLevelUnlocked : LevelActions
    data object Refresh : LevelActions
}