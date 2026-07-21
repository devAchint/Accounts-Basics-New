package com.techuntried.accountsbasics2.domain.model.level

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchChaptersResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val chapters: List<ChapterApiResponse>
)

@Serializable
data class FetchChapterResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val chapter: ChapterApiResponse
)

@Serializable
data class ChapterApiResponse(
    val subjectId: Int,
    val chapterId: Int,
    val name: String,
    val module:Int,
    val type:String,
)