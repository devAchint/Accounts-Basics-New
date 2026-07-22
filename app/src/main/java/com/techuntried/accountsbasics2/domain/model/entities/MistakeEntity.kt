package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity
import com.techuntried.accountsbasics2.domain.model.content.Option

@Entity(
    tableName = "mistakes",
    primaryKeys = ["subjectId", "chapterId", "questionId"]
)
data class MistakeEntity(
    val subjectId: Int,
    val chapterId: Int,
    val questionId: Int,
    val correctOptionId: Int,
    val questionText: String,
    val options: List<Option>,
    val answeredTimeInMillis: Long,
    val explanation:String,
    val userAnswer: String?,
    val fixed: Boolean
)