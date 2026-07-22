package com.techuntried.accountsbasics2.data.repository

import android.util.Log
import com.techuntried.accountsbasics2.data.database.SubjectDao
import com.techuntried.accountsbasics2.data.database.SubjectProgressDao
import com.techuntried.accountsbasics2.data.database.ChaptersDao
import com.techuntried.accountsbasics2.data.database.MistakeDao
import com.techuntried.accountsbasics2.domain.model.entities.SubjectEntity
import com.techuntried.accountsbasics2.domain.model.entities.SubjectProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.SubjectWithProgressEntity
import com.techuntried.accountsbasics2.domain.model.entities.ChapterEntity
import com.techuntried.accountsbasics2.domain.model.entities.MistakeEntity
import com.techuntried.accountsbasics2.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class RoomRepository @Inject constructor(
    private val subjectDao: SubjectDao,
    private val chaptersDao: ChaptersDao,
    private val subjectProgressDao: SubjectProgressDao,
    private val mistakeDao: MistakeDao
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

    fun observeLatestPlayedCategory(): Flow<SubjectWithProgressEntity?> {
        return subjectDao.observeLatestPlayedCategory()
            .catch { e ->
                if (e is CancellationException) throw e
                Log.e("Repo", "Error fetching latest category", e)
                emit(null)
            }
    }

    suspend fun fetchChapterDetailsBySubject(
        subjectId: Int,
        chapterId: Int
    ): ApiResult<ChapterEntity> {
        return try {
            ApiResult.Success(chaptersDao.getChapterDetailBySubject(subjectId, chapterId))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.d("MYDEBUG", "${e.message}")
            ApiResult.Error("Oops! Something went wrong while fetching levels. Please try again.")
        }
    }


    suspend fun updateChaptersCompleted(subjectId: Int, chaptersCompleted: Int) {
        safeRoomCall {
            val existingCompleted = subjectProgressDao.getChaptersCompleted(subjectId) ?: 0
            if (chaptersCompleted > existingCompleted) {
                subjectProgressDao.updateChaptersCompleted(
                    id = subjectId,
                    chaptersCompleted = chaptersCompleted,
                    lastPlayedTime = System.currentTimeMillis()
                )
            }
        }
    }

    suspend fun updateCorrectAnswered(subjectId: Int) {
        safeRoomCall {
            subjectProgressDao.updateCorrectAnswered(
                id = subjectId,
                lastPlayedTime = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateWrongAnswered(categoryId: Int) {
        safeRoomCall {
            subjectProgressDao.updateWrongAnswered(
                id = categoryId,
                lastPlayedTime = System.currentTimeMillis()
            )
        }
    }

    suspend fun getLevelsCompleted(categoryId: Int): Int {
        return safeRoomCall {
            if (subjectProgressDao.exists(categoryId)) {
                subjectProgressDao.getChaptersCompleted(categoryId) ?: 0
            } else {
                subjectProgressDao.insert(SubjectProgressEntity(subjectId = categoryId))
                0
            }
        } ?: 0
    }

    fun observeUserStats(): Flow<List<SubjectProgressEntity>> {
        return subjectProgressDao.observeAll().catch { e ->
            if (e is CancellationException) throw e
            Log.e("Repo", "Error fetching stats", e)
            emit(emptyList())
        }
    }

    suspend fun updateMistake(question: MistakeEntity) {
        safeRoomCall {
            mistakeDao.updateMistake(question)
        }
    }
    
    suspend fun insertMistake(question: MistakeEntity) {
        safeRoomCall {
            mistakeDao.insertMistake(question)
        }
    }

    suspend fun deleteMistake(subjectId: Int, chapterId: Int, questionId: Int) {
        safeRoomCall {
            mistakeDao.deleteMistake(subjectId, chapterId, questionId)
        }
    }


    fun getMistakes(
        subjectId: Int?
    ): Flow<ApiResult<List<MistakeEntity>>> {

        val flow = if (subjectId != null) {
            mistakeDao.getMistakesBySubject(subjectId)
        } else {
            mistakeDao.getMistakes()
        }

        return flow
            .map<List<MistakeEntity>, ApiResult<List<MistakeEntity>>> {
                ApiResult.Success(it)
            }
            .catch { e ->
                Log.d("MYDEBUG", e.message ?: "Unknown error")
                emit(
                    ApiResult.Error(
                        "Oops! Something went wrong while fetching wrong questions. Please try again."
                    )
                )
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