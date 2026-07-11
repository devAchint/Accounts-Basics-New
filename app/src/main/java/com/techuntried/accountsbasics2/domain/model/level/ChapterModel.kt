package com.techuntried.accountsbasics2.domain.model.level

data class ChapterModel(
    val subjectId: Int,
    val chapterId: Int,
    val name: String,
    val questions: Int,
    val module:Int,
    val type:String,
    val levelState: LevelState = if (chapterId == 1) LevelState.Unlocked else LevelState.Locked,
    val isLast: Boolean = false,
)

enum class LevelState {
    Locked, Completed, Unlocked
}