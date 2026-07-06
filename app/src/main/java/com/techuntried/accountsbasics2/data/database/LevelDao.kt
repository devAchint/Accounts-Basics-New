package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.LevelEntity

@Dao
interface LevelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Query("Delete FROM levels WHERE categoryId=:categoryId")
    suspend fun deleteLevels(categoryId: Int)

    @Query("SELECT * FROM levels WHERE categoryId=:categoryId")
    suspend fun getLevelsByCategory(categoryId: Int): List<LevelEntity>

    @Query("SELECT * FROM levels WHERE categoryId=:categoryId AND levelId=:levelId")
    suspend fun getLevelDetailByCategory(categoryId: Int, levelId: Int): LevelEntity

    @Query("SELECT * FROM levels WHERE categoryId=:categoryId AND  levelId=:levelId")
    suspend fun getLevel(categoryId: Int, levelId: Int): LevelEntity

    @Transaction
    suspend fun clearAndInsertLevels(categoryId: Int, levels: List<LevelEntity>) {
        deleteLevels(categoryId)
        insertLevels(levels)
    }
}