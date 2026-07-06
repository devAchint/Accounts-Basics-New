package com.techuntried.accountsbasics2.ui.navigation

import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel

data class ScoreArgs(
    val categoryId: Int,
    val levelId: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val questionReview: List<QuestionReviewModel>
)

data class RuleArgs(
    val categoryId: Int,
    val levelId: Int
)

data class GameArgs(
    val categoryId: Int,
    val levelId: Int,
    val timerCount: Int?
)

data class LevelArgs(
    val categoryId: Int,
    val categoryName: String,
    val showTopic: Boolean
)

fun Routes.GameScreenRoute.toGameArgs(): GameArgs {
    return GameArgs(
        categoryId = categoryId,
        levelId = levelId,
        timerCount = timerCount
    )
}

fun GameArgs.toGameScreenRoute(): Routes.GameScreenRoute {
    return Routes.GameScreenRoute(
        categoryId = categoryId,
        levelId = levelId,
        timerCount = timerCount
    )
}

fun Routes.ScoreScreenRoute.toScoreArgs(): ScoreArgs {
    return ScoreArgs(
        categoryId = categoryId,
        levelId = levelId,
        correctAnswers = correctAnswers,
        totalQuestions = totalQuestions,
        questionReview = questionReview
    )
}

fun ScoreArgs.toScoreScreenRoute(): Routes.ScoreScreenRoute {
    return Routes.ScoreScreenRoute(
        categoryId = categoryId,
        levelId = levelId,
        correctAnswers = correctAnswers,
        totalQuestions = totalQuestions,
        questionReview = questionReview
    )
}

fun Routes.RulesScreenRoute.toRuleArgs(): RuleArgs {
    return RuleArgs(
        categoryId = categoryId,
        levelId = levelId,
    )
}

fun RuleArgs.toRuleScreenRoute(): Routes.RulesScreenRoute {
    return Routes.RulesScreenRoute(
        categoryId = categoryId,
        levelId = levelId,
    )
}

fun Routes.LevelScreenRoute.toLevelArgs(): LevelArgs {
    return LevelArgs(
        categoryId = categoryId,
        categoryName = categoryName,
        showTopic = showTopic
    )
}

fun LevelArgs.toLevelScreenRoute(): Routes.LevelScreenRoute {
    return Routes.LevelScreenRoute(
        categoryId = categoryId,
        categoryName = categoryName,
        showTopic = showTopic
    )
}