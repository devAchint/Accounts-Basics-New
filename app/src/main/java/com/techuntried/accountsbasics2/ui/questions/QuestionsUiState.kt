package com.techuntried.accountsbasics2.ui.questions

import com.techuntried.accountsbasics2.domain.model.questions.GameOption
import com.techuntried.accountsbasics2.domain.model.questions.GameQuestionModel

sealed interface GameUiState {
    // Global properties available in ALL states
    val message: String?
    val actionLoading: Boolean
    val isSoundEnabled: Boolean

    // 1️⃣ Fetching questions from DB/Network
    data object Loading : GameUiState {
        override val message: String? = null
        override val actionLoading: Boolean = false
        override  val isSoundEnabled: Boolean = false
    }

    // 2️⃣ Fatal error (e.g., network failed, no questions found)
    data class Error(
        val errorMessage: String,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
        override val isSoundEnabled: Boolean = false
    ) : GameUiState

    // 3️⃣ The "3... 2... 1..." screen before the first question
    data class PreGameCountdown(
        val countdown: Int,
        override val message: String? = null,
        override val actionLoading: Boolean = false,
        override val isSoundEnabled: Boolean = false
    ) : GameUiState

    // 4️⃣ The actual interactive quiz
    data class ActiveGame(
        val currentQuestionIndex: Int,
        val currentQuestion: GameQuestionModel,
        val correctAnswers: Int = 0,
        val totalQuestions: Int,
        val isGameCompleted: Boolean = false,

        // Timers
        val gameTimer: Int? = null,
        val questionTimer: Int? = null,

        // Interactive state
        val answerState: AnswerState? = null,
        val currentQuestionCorrectAnswer: String? = null,
        val hideWrongs: List<GameOption>? = null, // 50/50 lifeline

        override val message: String? = null,
        override val isSoundEnabled: Boolean = false,
        val isHapticEnabled: Boolean = false,
        val isShowCorrectEnabled: Boolean = false,
        override val actionLoading: Boolean = false
    ) : GameUiState

    // 5️⃣ The final score and review screen
//    data class Completed(
//        val correctAnswers: Int,
//        val totalQuestions: Int,
//        val questionsReview: List<QuestionReviewModel>,
//
//        override val message: String? = null,
//        override val isSoundEnabled: Boolean = false,
//        override val isHapticEnabled: Boolean = false,
//        override val isShowCorrectEnabled: Boolean = false
//    ) : GameUiState
}

fun GameUiState.withMessage(newMessage: String?): GameUiState {
    return when (this) {
        is GameUiState.PreGameCountdown -> this.copy(message = newMessage)
        is GameUiState.ActiveGame -> this.copy(message = newMessage)
        is GameUiState.Error -> this.copy(message = newMessage)
        is GameUiState.Loading -> this
    }
}

fun GameUiState.withActionLoading(isLoading: Boolean): GameUiState {
    return when (this) {
        is GameUiState.ActiveGame -> this.copy(actionLoading = isLoading)
        is GameUiState.Error -> this.copy(actionLoading = isLoading)
        else -> this // Ignore for full-screen Loading state
    }
}

inline fun GameUiState.updatePreGameCountDown(block: GameUiState.PreGameCountdown.() -> GameUiState): GameUiState {
    return if (this is GameUiState.PreGameCountdown) this.block() else this
}

inline fun GameUiState.updateActiveGame(block: GameUiState.ActiveGame.() -> GameUiState): GameUiState {
    return if (this is GameUiState.ActiveGame) this.block() else this
}

