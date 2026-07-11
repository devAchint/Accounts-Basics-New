package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asLevelEntity
import com.techuntried.accountsbasics2.data.mappers.asLevelModel
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.level.ChapterModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetLevelDetailsUseCase @Inject constructor(
    private val roomRepository: RoomRepository,
    private val networkRepository: NetworkRepository,
    private val networkMonitor: NetworkMonitor // Better than passing 'context'
) {
    suspend operator fun invoke(categoryId: Int,levelId:Int): ApiResult<ChapterModel> {
        // 1. Check Local Source first
        val localResult = roomRepository.fetchLevelDetailsByCategory(categoryId,levelId)
        if (localResult is ApiResult.Success) {
            return ApiResult.Success(localResult.data.asLevelModel())
        }

        // 2. Check Connectivity
        if (!networkMonitor.isConnected()) {
            return ApiResult.Error("No internet connection")
        }

        // 3. Fetch from Remote
        return fetchLevelRemote(categoryId,levelId)
    }

    private suspend fun fetchLevelRemote(categoryId: Int,levelId: Int): ApiResult<ChapterModel> {
        return try {
            when (val response = networkRepository.fetchLevelsDetails(categoryId,levelId)) {
                is ApiResult.Success -> {
                    if (response.data.status) {
                        val model = response.data.level.asLevelEntity().asLevelModel()
                        ApiResult.Success(model)
                    } else {
                        ApiResult.Error(response.data.message)
                    }
                }
                is ApiResult.Error -> ApiResult.Error(response.errorMessage)
            }
        } catch (e: Exception) {
            ApiResult.Error("Oops! Something went wrong.")
        }
    }
}