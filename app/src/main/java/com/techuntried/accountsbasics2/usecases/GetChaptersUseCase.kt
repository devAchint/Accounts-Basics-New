package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asChapterEntity
import com.techuntried.accountsbasics2.data.mappers.asChapterModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.level.ChapterModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetChaptersUseCase @Inject constructor(
    private val repository: QuizRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(categoryId: Int): ApiResult<List<ChapterModel>> {
        val localResult = repository.getLocalLevels(categoryId)
        val hasLocalData = localResult is ApiResult.Success && localResult.data.isNotEmpty()
        val isOnline = networkMonitor.isConnected()

        if (!hasLocalData && !isOnline) {
            return ApiResult.Error("No internet connection")
        }

        // 1. Check if this specific category was already verified this session
        val needsSessionCheck = !repository.wasLevelsUpdatedThisSession(categoryId)
        val shouldAttemptSync = isOnline && (!hasLocalData || needsSessionCheck)


        if (shouldAttemptSync) {
            try {
                if (!hasLocalData || repository.levelsNeedsUpdate(categoryId)) {
                    val remoteResponse = repository.fetchRemoteChapters(categoryId)

                    if (remoteResponse is ApiResult.Success && remoteResponse.data.status) {
                        val entities = remoteResponse.data.levels.map { it.asChapterEntity() }
                        repository.saveLevels(categoryId, entities)
                        repository.markLevelsUpdatedForCategory(categoryId)
                        return ApiResult.Success(entities.map { it.asChapterModel() })
                    }
                } else {
                    repository.markLevelsUpdatedForCategory(categoryId)
                }
            } catch (e: Exception) {
                // If the network check fails, we don't crash; we just fall through to local data
            }
        }

        return if (hasLocalData) {
            ApiResult.Success(localResult.data.map { it.asChapterModel() })
        } else {
            ApiResult.Error("Failed to fetch chapters.")
        }
    }
}