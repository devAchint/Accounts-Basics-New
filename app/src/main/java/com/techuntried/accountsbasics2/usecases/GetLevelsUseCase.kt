package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asLevelEntity
import com.techuntried.accountsbasics2.data.mappers.asLevelModel
import com.techuntried.accountsbasics2.data.repository.QuizRepository
import com.techuntried.accountsbasics2.domain.model.level.LevelModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val repository: QuizRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(categoryId: Int): ApiResult<List<LevelModel>> {
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
                    val remoteResponse = repository.fetchRemoteLevels(categoryId)

                    if (remoteResponse is ApiResult.Success && remoteResponse.data.status) {
                        val entities = remoteResponse.data.levels.map { it.asLevelEntity() }
                        repository.saveLevels(categoryId, entities)
                        repository.markLevelsUpdatedForCategory(categoryId)
                        return ApiResult.Success(entities.map { it.asLevelModel() })
                    }
                } else {
                    repository.markLevelsUpdatedForCategory(categoryId)
                }
            } catch (e: Exception) {
                // If the network check fails, we don't crash; we just fall through to local data
            }
        }

        return if (hasLocalData) {
            ApiResult.Success(localResult.data.map { it.asLevelModel() })
        } else {
            ApiResult.Error("Failed to fetch categories.")
        }
    }
}