package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "subject_progress")
data class SubjectProgressEntity(
    @PrimaryKey
    val subjectId: Int,
    val chaptersCompleted: Int = 0,
    val correctAnswered: Int = 0,
    val wrongAnswered: Int = 0,
    val lastPlayedTime: Long = 0L
)