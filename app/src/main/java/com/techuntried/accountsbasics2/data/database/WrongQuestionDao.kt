package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.WrongQuestionEntity

@Dao
interface WrongQuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWrongQuestions(questions: List<WrongQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWrongQuestion(question: WrongQuestionEntity)

    @Query("Delete FROM wrong_questions WHERE subjectId=:subjectId AND chapterId=:chapterId AND questionId=:questionId")
    suspend fun deleteWrongQuestion(subjectId: Int, chapterId: Int, questionId: Int)

    @Query("Delete FROM wrong_questions WHERE subjectId=:subjectId AND chapterId=:chapterId")
    suspend fun deleteWrongQuestions(subjectId: Int, chapterId: Int)

    @Query("SELECT * FROM wrong_questions")
    suspend fun getWrongQuestions(): List<WrongQuestionEntity>

    @Query("SELECT * FROM wrong_questions WHERE subjectId=:subjectId")
    suspend fun getWrongQuestionsBySubject(subjectId: Int): List<WrongQuestionEntity>

    @Transaction
    suspend fun clearAndInsertWrongQuestions(
        subjectId: Int,
        chapterId: Int,
        questions: List<WrongQuestionEntity>
    ) {
        deleteWrongQuestions(subjectId, chapterId)
        insertWrongQuestions(questions)
    }
}