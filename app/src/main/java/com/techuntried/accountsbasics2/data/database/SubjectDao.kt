package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryWithProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Query("Delete FROM subjects")
    suspend fun deleteSubjects()

    @Query("Delete FROM subjects WHERE course = :course")
    suspend fun deleteSubjects(course: Int)

    @Query("SELECT * FROM subjects")
    suspend fun getSubjects(): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE course=:course")
    suspend fun getSubjectsByGrades(course: Int): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE categoryId = :id")
    suspend fun getSubjectById(id:Int): SubjectEntity

    @Transaction
    suspend fun clearAndInsertSubjects(subjects: List<SubjectEntity>) {
        deleteSubjects()
        insertSubjects(subjects)
    }

    @Transaction
    @Query("""
    SELECT c.*
    FROM subjects c
    INNER JOIN category_progress cp
        ON c.categoryId = cp.categoryId
    ORDER BY cp.lastPlayedTime DESC
    LIMIT 1
""")
    fun observeLatestPlayedCategory(): Flow<CategoryWithProgressEntity?>


    @Query("SELECT * FROM subjects Where categoryId = :categoryId")
    suspend fun getCategory(categoryId: Int): SubjectEntity

}