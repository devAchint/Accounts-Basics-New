package com.techuntried.accountsbasics2.domain.model.subjects

data class SubjectModel(
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
    val chapters: Int,
    val course: Int?
)