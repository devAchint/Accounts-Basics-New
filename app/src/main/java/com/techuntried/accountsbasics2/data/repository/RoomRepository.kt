package com.techuntried.accountsbasics2.data.repository

import android.util.Log
import com.techuntried.accountsbasics2.data.database.SubjectDao
import com.techuntried.accountsbasics2.data.database.CategoryProgressDao
import com.techuntried.accountsbasics2.data.database.ChaptersDao
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.CategoryWithProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class RoomRepository @Inject constructor(
    private val subjectDao: SubjectDao,
    private val chaptersDao: ChaptersDao,
    private val categoryProgressDao: CategoryProgressDao
) {

    suspend fun fetchSubjectById(categoryId: Int): ApiResult<SubjectEntity> {
        return try {
            ApiResult.Success(subjectDao.getSubjectById(categoryId))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching category. Please try again.")
        }
    }

    fun observeLatestPlayedCategory(): Flow<CategoryWithProgressEntity?> {
        return subjectDao.observeLatestPlayedCategory()
            .catch {e ->
                if (e is CancellationException) throw e
                Log.e("Repo", "Error fetching latest category", e)
                emit(null)
            }
    }

    suspend fun fetchChapterDetailsBySubject(subjectId: Int, chapterId: Int): ApiResult<ChapterEntity> {
        return try {
            ApiResult.Success(chaptersDao.getChapterDetailBySubject(subjectId, chapterId))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching levels. Please try again.")
        }
    }


    suspend fun updateLevelsCompleted(categoryId: Int, levelCompleted: Int) {
        safeRoomCall {
            val levels = categoryProgressDao.getLevelsCompleted(categoryId) ?: 0
            if (levelCompleted > levels) {
                categoryProgressDao.updateLevelsCompleted(
                    id = categoryId,
                    levelsCompleted = levelCompleted,
                    lastPlayedTime = System.currentTimeMillis()
                )
            }
        }
    }

    suspend fun updateCorrectAnswered(categoryId: Int) {
        safeRoomCall {
            categoryProgressDao.updateCorrectAnswered(
                id = categoryId,
                lastPlayedTime = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateWrongAnswered(categoryId: Int) {
        safeRoomCall {
            categoryProgressDao.updateWrongAnswered(
                id = categoryId,
                lastPlayedTime = System.currentTimeMillis()
            )
        }
    }

    suspend fun getLevelsCompleted(categoryId: Int): Int {
        return safeRoomCall {
            if (categoryProgressDao.exists(categoryId)) {
                categoryProgressDao.getLevelsCompleted(categoryId) ?: 0
            } else {
                categoryProgressDao.insert(CategoryProgressEntity(categoryId = categoryId))
                0
            }
        } ?: 0
    }

    fun observeUserStats(): Flow<List<CategoryProgressEntity>> {
        return categoryProgressDao.observeAll().catch {e->
            if (e is CancellationException) throw e
            Log.e("Repo", "Error fetching stats", e)
            emit(emptyList())
        }
    }

}


suspend fun <T> safeRoomCall(
    errorMsg: String = "Operation failed",
    operation: suspend () -> T
): T? {
    return try {
        operation()
    } catch (e: Exception) {
        // 1. Let coroutine cancellations pass through smoothly
        if (e is CancellationException) throw e

        // 2. Log the actual database error
        Log.e("MYDEBUG", "$errorMsg: ${e.message}")

        // 3. Return null so the app doesn't crash
        null
    }
}