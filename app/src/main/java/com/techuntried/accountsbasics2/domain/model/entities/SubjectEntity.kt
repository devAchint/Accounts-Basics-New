package com.techuntried.accountsbasics2.domain.model.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey
    val subjectId: Int,
    val name: String,
    val imageUrl: String,
    val isFeatured: Boolean,
    val bgColor: String? = null,
    val active: Boolean,
    val section: String?,
    val weight: Int,
    val chapters: Int,
    val tag: String? = null,
    val showTopics: Boolean,
    val sectionWeight: Int,
    val featuredWeight: Int,
    val course: Int?,
)


data class SubjectWithProgressEntity(

    @Embedded
    val subject: SubjectEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "subjectId"
    )
    val progress: SubjectProgressEntity?
)

