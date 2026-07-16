package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.LearnContentEntity

@Dao
interface LearnContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: List<LearnContentEntity>)

    @Query("Delete FROM learnContent WHERE subjectId=:categoryId AND chapterId =:chapterId")
    suspend fun deleteContent(categoryId: Int, chapterId: Int)

    @Query("SELECT * FROM learnContent WHERE subjectId=:subjectId AND chapterId =:chapterId")
    suspend fun getContent(subjectId: Int, chapterId: Int): List<LearnContentEntity>

    @Transaction
    suspend fun clearAndInsertContent(
        subjectId: Int,
        chapterId: Int,
        questions: List<LearnContentEntity>
    ) {
        deleteContent(subjectId, chapterId)
        insertContent(questions)
    }
}