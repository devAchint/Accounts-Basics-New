package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "levels", primaryKeys = ["categoryId", "levelId"])
data class LevelEntity(
    @ColumnInfo(name = "levelId")
    val levelId: Int,
    @ColumnInfo(name = "categoryId")
    val categoryId: Int,
    @ColumnInfo(name = "levelName")
    val levelName: String,
    @ColumnInfo(name = "questions")
    val questions: Int,
    @ColumnInfo(name = "topic")
    val topic: String?
)