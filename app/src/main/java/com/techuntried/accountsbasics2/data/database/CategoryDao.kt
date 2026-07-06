package com.techuntried.accountsbasics2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techuntried.accountsbasics2.domain.model.entities.CategoryEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryWithProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("Delete FROM categories")
    suspend fun deleteCategories()

    @Query("Delete FROM categories WHERE grade = :grade")
    suspend fun deleteCategories(grade: Int)

    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE grade IN (:grades)")
    suspend fun getCategoriesByGrades(grades: List<Int>): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id:Int): CategoryEntity

    @Transaction
    suspend fun clearAndInsertCategories(categories: List<CategoryEntity>) {
        deleteCategories()
        insertCategories(categories)
    }

    @Transaction
    @Query("""
    SELECT c.*
    FROM categories c
    INNER JOIN category_progress cp
        ON c.categoryId = cp.categoryId
    ORDER BY cp.lastPlayedTime DESC
    LIMIT 1
""")
    fun observeLatestPlayedCategory(): Flow<CategoryWithProgressEntity?>


    @Query("SELECT * FROM categories Where categoryId = :categoryId")
    suspend fun getCategory(categoryId: Int): CategoryEntity

}