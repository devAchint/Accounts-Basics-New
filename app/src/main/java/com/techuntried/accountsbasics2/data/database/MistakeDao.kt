package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.techuntried.accountsbasics2.domain.model.entities.MistakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMistakes(questions: List<MistakeEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMistake(question: MistakeEntity)

    @Update
    suspend fun updateMistake(question: MistakeEntity)

    @Query("Delete FROM mistakes WHERE subjectId=:subjectId AND chapterId=:chapterId AND questionId=:questionId")
    suspend fun deleteMistake(subjectId: Int, chapterId: Int, questionId: Int)

    @Query("Delete FROM mistakes WHERE subjectId=:subjectId AND chapterId=:chapterId")
    suspend fun deleteMistakes(subjectId: Int, chapterId: Int)

    @Query("SELECT * FROM mistakes")
    fun getMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE subjectId=:subjectId")
    fun getMistakesBySubject(subjectId: Int): Flow<List<MistakeEntity>>

    @Transaction
    suspend fun clearAndInsertMistakes(
        subjectId: Int,
        chapterId: Int,
        questions: List<MistakeEntity>
    ) {
        deleteMistakes(subjectId, chapterId)
        insertMistakes(questions)
    }
}