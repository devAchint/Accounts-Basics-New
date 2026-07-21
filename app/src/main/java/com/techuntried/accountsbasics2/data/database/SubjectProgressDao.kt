package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.techuntried.accountsbasics2.domain.model.entities.SubjectProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectProgressDao {

    /* -------------------- INSERT -------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: SubjectProgressEntity)

    /* -------------------- READ -------------------- */

    @Query("SELECT * FROM subject_progress ORDER BY lastPlayedTime DESC")
    fun observeAll(): Flow<List<SubjectProgressEntity>>

    @Query("SELECT chaptersCompleted FROM subject_progress WHERE subjectId=:id")
    suspend fun getChaptersCompleted(id: Int): Int?

    @Query("SELECT * FROM subject_progress")
    suspend fun getAll(): List<SubjectProgressEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM subject_progress WHERE subjectId = :subjectId)")
    suspend fun exists(subjectId: Int): Boolean

    /* -------------------- UPDATE (SINGLE SOURCE OF TRUTH) -------------------- */

    @Update
    suspend fun update(progress: SubjectProgressEntity)

    @Query("UPDATE subject_progress SET chaptersCompleted=:chaptersCompleted, lastPlayedTime=:lastPlayedTime WHERE subjectId=:id")
    suspend fun updateChaptersCompleted(id: Int, chaptersCompleted: Int, lastPlayedTime: Long)

    @Query("UPDATE subject_progress SET correctAnswered=correctAnswered+1,lastPlayedTime=:lastPlayedTime WHERE subjectId=:id")
    suspend fun updateCorrectAnswered(id: Int, lastPlayedTime: Long)

    @Query("UPDATE subject_progress SET wrongAnswered=wrongAnswered+1,lastPlayedTime=:lastPlayedTime WHERE subjectId=:id")
    suspend fun updateWrongAnswered(id: Int, lastPlayedTime: Long)

    /* -------------------- DELETE -------------------- */

    @Query("DELETE FROM subject_progress")
    suspend fun clearAll()

    /* -------------------- REPLACE (SYNC / RESTORE) -------------------- */

    @Transaction
    suspend fun replaceAll(progressList: List<SubjectProgressEntity>) {
        clearAll()
        progressList.forEach { insert(it) }
    }
}
