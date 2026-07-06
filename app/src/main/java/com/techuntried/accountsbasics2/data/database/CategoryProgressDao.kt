package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.techuntried.accountsbasics2.domain.model.entities.CategoryProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryProgressDao {

    /* -------------------- INSERT -------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: CategoryProgressEntity)

    /* -------------------- READ -------------------- */

    @Query("SELECT * FROM category_progress ORDER BY lastPlayedTime DESC")
    fun observeAll(): Flow<List<CategoryProgressEntity>>

    @Query("SELECT levelsPlayed FROM category_progress WHERE categoryId=:id")
    suspend fun getLevelsCompleted(id: Int): Int?

    @Query("SELECT * FROM category_progress")
    suspend fun getAll(): List<CategoryProgressEntity>

    @Query("SELECT * FROM category_progress WHERE categoryId = :categoryId")
    suspend fun getByCategoryId(categoryId: Int): CategoryProgressEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM category_progress WHERE categoryId = :categoryId)")
    suspend fun exists(categoryId: Int): Boolean

    /* -------------------- UPDATE (SINGLE SOURCE OF TRUTH) -------------------- */

    @Update
    suspend fun update(progress: CategoryProgressEntity)

    @Query("UPDATE category_progress SET levelsPlayed=:levelsCompleted, lastPlayedTime=:lastPlayedTime WHERE categoryId=:id")
    suspend fun updateLevelsCompleted(id: Int, levelsCompleted: Int, lastPlayedTime: Long)

    @Query("UPDATE category_progress SET correctAnswered=correctAnswered+1,lastPlayedTime=:lastPlayedTime WHERE categoryId=:id")
    suspend fun updateCorrectAnswered(id: Int, lastPlayedTime: Long)

    @Query("UPDATE category_progress SET wrongAnswered=wrongAnswered+1,lastPlayedTime=:lastPlayedTime WHERE categoryId=:id")
    suspend fun updateWrongAnswered(id: Int, lastPlayedTime: Long)

    /* -------------------- DELETE -------------------- */

    @Query("DELETE FROM category_progress")
    suspend fun clearAll()

    /* -------------------- REPLACE (SYNC / RESTORE) -------------------- */

    @Transaction
    suspend fun replaceAll(progressList: List<CategoryProgressEntity>) {
        clearAll()
        progressList.forEach { insert(it) }
    }
}
