package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "levels", primaryKeys = ["categoryId", "levelId"])
data class ChapterEntity(
    @ColumnInfo(name = "levelId")
    val chapterId: Int,
    @ColumnInfo(name = "categoryId")
    val subjectId: Int,
    @ColumnInfo(name = "levelName")
    val name: String,
    @ColumnInfo(name = "questions")
    val questions: Int,
    @ColumnInfo(name = "module")
    val module:Int,
    @ColumnInfo(name = "type")
    val type:String,
)