package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity
import com.techuntried.accountsbasics2.domain.model.content.Option

@Entity(
    tableName = "questions",
    primaryKeys = ["subjectId", "chapterId", "questionId"]
)
data class QuestionEntity(
    val subjectId: Int,
    val chapterId: Int,
    val questionId: Int,
    val correctOptionId: Int,
    val questionText: String,
    val options: List<Option>,
    val explanation: String
)