package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "category_progress")
data class CategoryProgressEntity(
    @PrimaryKey
    val categoryId: Int,
    val levelsPlayed: Int = 0,
    val correctAnswered: Int = 0,
    val wrongAnswered: Int = 0,
    val lastPlayedTime: Long = 0L
)