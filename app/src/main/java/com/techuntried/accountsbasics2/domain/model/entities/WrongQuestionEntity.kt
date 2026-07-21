package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity
import com.techuntried.accountsbasics2.domain.model.content.Option

@Entity(
    tableName = "wrong_questions",
    primaryKeys = ["subjectId", "chapterId", "questionId"]
)
data class WrongQuestionEntity(
    val subjectId: Int,
    val chapterId: Int,
    val questionId: Int,
    val correctOptionId: Int,
    val questionText: String,
    val options: List<Option>
)