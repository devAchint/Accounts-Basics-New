package com.techuntried.accountsbasics2.domain.model.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchCategoriesResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val categories: List<CategoryApiResponse>
)

@Serializable
data class FetchCategoryResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val category: CategoryApiResponse
)

@Serializable
data class CategoryApiResponse(
    val categoryId: Int,
    val categoryName: String,
    val categoryImage: String,
    val isFeatured: Boolean,
    val bgColor: String?,
    val active: Boolean,
    val showTopics: Boolean,
    val section: String?,
    val tag: String?,
    val weight: Int,
    val sectionWeight: Int,
    val featuredWeight: Int,
    val levels: Int,
    val grades: List<Int>
)