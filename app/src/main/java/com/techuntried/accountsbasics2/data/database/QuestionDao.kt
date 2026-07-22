package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.QuestionEntity

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("Delete FROM questions WHERE subjectId=:subjectId AND chapterId=:chapterId")
    suspend fun deleteQuestions(subjectId: Int, chapterId: Int)

    @Query("SELECT * FROM questions WHERE subjectId=:subjectId AND chapterId=:chapterId")
    suspend fun getQuestions(subjectId: Int, chapterId: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE subjectId=:subjectId AND chapterId=:chapterId AND questionId=:questionId")
    suspend fun getSingleQuestion(
        subjectId: Int,
        chapterId: Int,
        questionId: Int
    ): List<QuestionEntity>

    @Transaction
    suspend fun clearAndInsertQuestions(
        subjectId: Int,
        chapterId: Int,
        questions: List<QuestionEntity>
    ) {
        deleteQuestions(subjectId, chapterId)
        insertQuestions(questions)
    }
}