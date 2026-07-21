package com.techuntried.accountsbasics2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.SubjectProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.domain.model.entities.LearnContentEntity
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity
import com.techuntried.accountsbasics2.domain.model.entities.WrongQuestionEntity

@Database(
    entities = [
        SubjectEntity::class,
        ChapterEntity::class,
        QuestionEntity::class,
        SubjectProgressEntity::class,
        LearnContentEntity::class,
        WrongQuestionEntity::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectProgressDao(): SubjectProgressDao
    abstract fun subjectDao(): SubjectDao
    abstract fun chaptersDao(): ChaptersDao
    abstract fun questionDao(): QuestionDao

    abstract fun learnContentDao(): LearnContentDao

    abstract fun wrongQuestionDao(): WrongQuestionDao
}