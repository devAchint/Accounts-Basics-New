package com.techuntried.accountsbasics2.domain.model.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchLearnContentResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val questions: List<LearnContentApiResponse>
)

@Serializable
data class LearnContentApiResponse(
    val contentId: Int,
    val subjectId: Int,
    val chapterId: Int,
    val page: Int,
    val title: String,
    val content: List<ContentItem>
)

@Serializable
sealed interface ContentItem