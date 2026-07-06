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

    @Query("Delete FROM questions WHERE categoryId=:categoryId AND levelId=:levelId")
    suspend fun deleteQuestions(categoryId: Int, levelId: Int)

    @Query("SELECT * FROM questions WHERE categoryId=:categoryId AND levelId=:levelId")
    suspend fun getQuestions(categoryId: Int, levelId: Int): List<QuestionEntity>

    @Transaction
    suspend fun clearAndInsertQuestions(categoryId: Int, levelId:Int, questions: List<QuestionEntity>) {
        deleteQuestions(categoryId,levelId)
        insertQuestions(questions)
    }
}