package com.techuntried.accountsbasics2.domain.model.level

data class LevelModel(
    val categoryId: Int,
    val levelId: Int,
    val levelName: String,
    val questions: Int,
    val levelState: LevelState = if (levelId == 1) LevelState.Unlocked else LevelState.Locked,
    val isLast: Boolean = false,
    val topic:String?
)

enum class LevelState {
    Locked, Completed, Unlocked
}