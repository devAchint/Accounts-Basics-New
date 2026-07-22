package com.techuntried.accountsbasics2.ui.navigation


data class ChapterArgs(
    val subjectId: Int,
    val subjectName: String,
    val showTopic: Boolean
)


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