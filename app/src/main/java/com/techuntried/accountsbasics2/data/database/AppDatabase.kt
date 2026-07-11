package com.techuntried.accountsbasics2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.LevelEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity

@Database(
    entities = [
        SubjectEntity::class,
        LevelEntity::class,
        QuestionEntity::class,
        CategoryProgressEntity::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryProgressDao(): CategoryProgressDao
    abstract fun subjectDao(): SubjectDao
    abstract fun levelDao(): LevelDao
    abstract fun questionDao(): QuestionDao
}