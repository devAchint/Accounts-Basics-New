package com.techuntried.accountsbasics2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.domain.model.entities.LearnContentEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity

@Database(
    entities = [
        SubjectEntity::class,
        ChapterEntity::class,
        QuestionEntity::class,
        CategoryProgressEntity::class,
        LearnContentEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryProgressDao(): CategoryProgressDao
    abstract fun subjectDao(): SubjectDao
    abstract fun levelDao(): ChaptersDao
    abstract fun questionDao(): QuestionDao

    abstract fun learnContentDao(): LearnContentDao
}