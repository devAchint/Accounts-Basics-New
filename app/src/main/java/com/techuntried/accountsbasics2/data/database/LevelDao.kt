package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity

@Dao
interface LevelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<ChapterEntity>)

    @Query("Delete FROM levels WHERE categoryId=:categoryId")
    suspend fun deleteLevels(categoryId: Int)

    @Query("SELECT * FROM levels WHERE categoryId=:categoryId")
    suspend fun getLevelsByCategory(categoryId: Int): List<ChapterEntity>

    @Query("SELECT * FROM levels WHERE categoryId=:categoryId AND levelId=:levelId")
    suspend fun getLevelDetailByCategory(categoryId: Int, levelId: Int): ChapterEntity

    @Query("SELECT * FROM levels WHERE categoryId=:categoryId AND  levelId=:levelId")
    suspend fun getLevel(categoryId: Int, levelId: Int): ChapterEntity

    @Transaction
    suspend fun clearAndInsertLevels(categoryId: Int, levels: List<ChapterEntity>) {
        deleteLevels(categoryId)
        insertLevels(levels)
    }
}