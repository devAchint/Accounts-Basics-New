package com.techuntried.accountsbasics2.domain.model.category

data class CategoryModel(
    val categoryId: Int,
    val categoryName: String,
    val featured: Boolean,
    val imageUrl: String,
    val bgColor: String?,
    val active: Boolean,
    val showTopics: Boolean,
    val section: String?,
    val tag: String?,
    val weight: Int,
    val sectionWeight: Int,
    val featuredWeight: Int,
    val levels: Int,
    val grade: Int
)