package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity

@Entity(tableName = "chapters", primaryKeys = ["chapterId", "subjectId"])
data class ChapterEntity(
    val chapterId: Int,
    val subjectId: Int,
    val name: String,
    val module: Int,
    val type: String
)