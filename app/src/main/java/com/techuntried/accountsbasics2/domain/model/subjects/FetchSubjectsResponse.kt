package com.techuntried.accountsbasics2.domain.model.subjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchSubjectsResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val categories: List<SubjectApiResponse>
)

@Serializable
data class FetchSubjectResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val category: SubjectApiResponse
)

@Serializable
data class SubjectApiResponse(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val featured: Boolean,
    val bgColor: String?,
    val active: Boolean,
    val showTopics: Boolean,
    val section: String?,
    val tag: String?,
    val weight: Int,
    val sectionWeight: Int,
    val featuredWeight: Int,
    val chapters: Int,
    val courseId: Int
)