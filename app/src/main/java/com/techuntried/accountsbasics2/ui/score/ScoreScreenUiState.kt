package com.techuntried.accountsbasics2.ui.score

import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel

sealed interface ScoreScreenUiState {
    val message: String?
    val actionLoading:Boolean

    data object Loading : ScoreScreenUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
    }


    data class Success(
        val score: ScoreModel,
        val review: List<QuestionReviewModel> = emptyList(),
        val scoreRatingEnabled: Boolean = false,
        val levelRatingEnabled: Boolean = false,
        override val message: String? = null,
        override val actionLoading: Boolean = false
    ) : ScoreScreenUiState

    data class Error(
        val errorMessage: String,
        override val message: String? = null,
        override val actionLoading: Boolean = false
    ) : ScoreScreenUiState
}

fun ScoreScreenUiState.withMessage(newMessage: String?): ScoreScreenUiState {
    return when (this) {
        is ScoreScreenUiState.Success -> this.copy(message = newMessage)
        is ScoreScreenUiState.Error -> this.copy(message = newMessage)
        is ScoreScreenUiState.Loading -> this
    }
}

fun ScoreScreenUiState.withActionLoading(isLoading: Boolean): ScoreScreenUiState {
    return when (this) {
        is ScoreScreenUiState.Success -> this.copy(actionLoading = isLoading)
        is ScoreScreenUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}


inline fun ScoreScreenUiState.updateSuccess(
    block: ScoreScreenUiState.Success.() -> ScoreScreenUiState
): ScoreScreenUiState {
    return if (this is ScoreScreenUiState.Success) this.block() else this
}

sealed interface ScoreActions {
    data object Refresh : ScoreActions
    data class AddCoins(val coins: Int) : ScoreActions
    data object DismissScoreRating : ScoreActions
    data object DismissLevelRating : ScoreActions
    data class SubmitLevelRating(val ratingText: String) : ScoreActions
    data class SubmitAppRating(val ratingText: String) : ScoreActions
}