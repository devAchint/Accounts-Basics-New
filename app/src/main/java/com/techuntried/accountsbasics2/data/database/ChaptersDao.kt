package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity

@Dao
interface ChaptersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<ChapterEntity>)

    @Query("Delete FROM chapters WHERE subjectId=:categoryId")
    suspend fun deleteLevels(categoryId: Int)

    @Query("SELECT * FROM chapters WHERE subjectId=:subjectId")
    suspend fun getChaptersBySubject(subjectId: Int): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE subjectId=:subjectId AND chapterId=:chapterId")
    suspend fun getChapterDetailBySubject(subjectId: Int, chapterId: Int): ChapterEntity

    @Transaction
    suspend fun clearAndInsertLevels(categoryId: Int, levels: List<ChapterEntity>) {
        deleteLevels(categoryId)
        insertLevels(levels)
    }
}