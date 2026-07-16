package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Entity
import com.techuntried.accountsbasics2.domain.model.content.ContentItem

@Entity(
    tableName = "learnContent",
    primaryKeys = ["subjectId", "chapterId", "contentId"]
)
data class LearnContentEntity(
    val contentId: Int,
    val subjectId: Int,
    val chapterId: Int,
    val page: Int,
    val title: String,
    val content: List<ContentItem>
)
