package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.techuntried.accountsbasics2.domain.model.question.Option

@Entity(
    tableName = "questions",
    primaryKeys = ["categoryId", "levelId", "questionId"]
)
data class QuestionEntity(
    @ColumnInfo(name = "categoryId") val categoryId: Int,
    @ColumnInfo(name = "levelId") val levelId: Int,
    @ColumnInfo(name = "questionId") val questionId: Int,
    @ColumnInfo(name = "correctOptionId") val correctOptionId: Int,
    @ColumnInfo(name = "questionText") val questionText: String,
    @ColumnInfo(name = "options") val options: List<Option>
)