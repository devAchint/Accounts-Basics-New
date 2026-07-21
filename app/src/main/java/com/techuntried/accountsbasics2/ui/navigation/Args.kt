package com.techuntried.accountsbasics2.ui.navigation

import com.techuntried.accountsbasics2.domain.model.questions.QuestionReviewModel

data class ScoreArgs(
    val subjectId: Int,
    val chapterId: Int,
    val isPracticeType:Boolean,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val questionReview: List<QuestionReviewModel>
)

data class RuleArgs(
    val subjectId: Int,
    val chapterId: Int
)

data class QuestionsArgs(
    val subjectId: Int,
    val chapterId: Int,
    val timerCount: Int?
)

data class ChapterArgs(
    val subjectId: Int,
    val subjectName: String,
    val showTopic: Boolean
)

fun Routes.QuestionsScreenRoute.toQuestionArgs(): QuestionsArgs {
    return QuestionsArgs(
        subjectId = subjectId,
        chapterId = chapterId,
        timerCount = timerCount
    )
}

fun QuestionsArgs.toQuestionsScreenRoute(): Routes.QuestionsScreenRoute {
    return Routes.QuestionsScreenRoute(
        subjectId = subjectId,
        chapterId = chapterId,
        timerCount = timerCount
    )
}

fun Routes.ScoreScreenRoute.toScoreArgs(): ScoreArgs {
    return ScoreArgs(
        subjectId = subjectId,
        chapterId = chapterId,
        correctAnswers = correctAnswers,
        totalQuestions = totalQuestions,
        questionReview = questionReview,
        isPracticeType = isPracticeType
    )
}

fun ScoreArgs.toScoreScreenRoute(): Routes.ScoreScreenRoute {
    return Routes.ScoreScreenRoute(
        subjectId = subjectId,
        chapterId = chapterId,
        correctAnswers = correctAnswers,
        totalQuestions = totalQuestions,
        questionReview = questionReview,
        isPracticeType = isPracticeType
    )
}

fun Routes.RulesScreenRoute.toRuleArgs(): RuleArgs {
    return RuleArgs(
        subjectId = subjectId,
        chapterId = chapterId,
    )
}

fun RuleArgs.toRuleScreenRoute(): Routes.RulesScreenRoute {
    return Routes.RulesScreenRoute(
        subjectId = subjectId,
        chapterId = chapterId,
    )
}

fun Routes.ChaptersScreenRoute.toLevelArgs(): ChapterArgs {
    return ChapterArgs(
        subjectId = subjectId,
        subjectName = subjectName,
        showTopic = showTopic
    )
}

fun ChapterArgs.toLevelScreenRoute(): Routes.ChaptersScreenRoute {
    return Routes.ChaptersScreenRoute(
        subjectId = subjectId,
        subjectName = subjectName,
        showTopic = showTopic
    )
}