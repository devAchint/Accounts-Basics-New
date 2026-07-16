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
sealed interface ContentItem {

    @Serializable
    @SerialName("heading")
    data class Heading(
        val text: String
    ) : ContentItem

    @Serializable
    @SerialName("subHeading")
    data class SubHeading(
        val text: String
    ) : ContentItem

    @Serializable
    @SerialName("paragraph")
    data class Paragraph(
        val text: String
    ) : ContentItem

    @Serializable
    @SerialName("bulletList")
    data class BulletList(
        val items: List<String>
    ) : ContentItem

    @Serializable
    @SerialName("note")
    data class Note(
        val text: String
    ) : ContentItem

    @Serializable
    @SerialName("tip")
    data class Tip(
        val text: String
    ) : ContentItem

    @Serializable
    @SerialName("image")
    data class Image(
        val imageUrl: String,
        val caption: String? = null
    ) : ContentItem

    @Serializable
    @SerialName("table")
    data class Table(
        val headers: List<String>,
        val rows: List<List<String>>
    ) : ContentItem

    @Serializable
    @SerialName("quote")
    data class Quote(
        val text: String
    ) : ContentItem

    @Serializable
    @SerialName("summary")
    data class Summary(
        val text: String
    ) : ContentItem
}


data class LearnContentModel(
    val contentId: Int,
    val subjectId: Int,
    val chapterId: Int,
    val page: Int,
    val title: String,
    val content: List<ContentItem>
)