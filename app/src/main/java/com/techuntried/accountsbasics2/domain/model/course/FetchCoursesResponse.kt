package com.techuntried.accountsbasics2.domain.model.course

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class FetchCoursesResponse(
    val message: String,
    val status: Boolean,
    val data: List<CourseResponse>
)

@Serializable
data class CourseResponse(
    val id: Int,
    val name: String,
    val description: String,
    val image: String
)