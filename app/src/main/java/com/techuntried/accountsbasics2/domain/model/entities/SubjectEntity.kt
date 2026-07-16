package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey
    val categoryId: Int,
    @ColumnInfo(name = "category_name")
    val categoryName: String,
    @ColumnInfo(name = "category_image")
    val categoryImage: String,
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean,
    @ColumnInfo(name = "bgColor")
    val bgColor: String? = null,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "section")
    val section: String?,
    @ColumnInfo(name = "weight")
    val weight: Int,
    @ColumnInfo(name = "levels")
    val levels: Int,
    @ColumnInfo(name = "tag")
    val tag: String? = null,
    @ColumnInfo(name = "showTopics")
    val showTopics: Boolean,
    @ColumnInfo(name = "sectionWeight")
    val sectionWeight: Int,
    @ColumnInfo(name = "featuredWeight")
    val featuredWeight: Int,
    @ColumnInfo(name = "course")
    val course: Int?,
)


data class CategoryWithProgressEntity(

    @Embedded
    val category: SubjectEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val progress: CategoryProgressEntity?
)

