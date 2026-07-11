package com.techuntried.accountsbasics2.domain.model

import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import java.util.concurrent.TimeUnit

data class CategoryProgressModel(
    val categoryId: Int,
    val levelsPlayed: Int = 0,
    val correctAnswered: Int = 0,
    val wrongAnswered: Int = 0,
    val lastPlayedTime: Long = 0L,
    val progressPercentage: Float = 0f,
    val accuracy: Float = 0f
)

data class CategoryWithProgressModel(
    val category: SubjectModel,
    val progress: CategoryProgressModel
) {
    fun lastPlayedText(): String {
        if (progress.lastPlayedTime == 0L) return "Never played"

        val now = System.currentTimeMillis()

        val diffMillis = now - progress.lastPlayedTime
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            diffDays <= 0L -> "Today"
            diffDays == 1L -> "Yesterday"
            else -> "$diffDays days ago"
        }
    }
}