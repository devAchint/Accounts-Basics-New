package com.techuntried.accountsbasics2.domain.model.level

data class ChapterModel(
    val subjectId: Int,
    val chapterId: Int,
    val name: String,
    val module:Int,
    val type:String,
    val chapterState: ChapterState = if (chapterId == 1) ChapterState.Unlocked else ChapterState.Locked,
    val isLast: Boolean = false,
)

enum class ChapterState {
    Locked, Completed, Unlocked
}