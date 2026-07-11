package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asChapterEntity
import com.techuntried.accountsbasics2.data.mappers.asChapterModel
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.level.ChapterModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetChapterDetailsUseCase @Inject constructor(
    private val roomRepository: RoomRepository,
    private val networkRepository: NetworkRepository,
    private val networkMonitor: NetworkMonitor // Better than passing 'context'
) {
    suspend operator fun invoke(categoryId: Int,levelId:Int): ApiResult<ChapterModel> {
        // 1. Check Local Source first
        val localResult = roomRepository.fetchChapterDetailsBySubject(categoryId,levelId)
        if (localResult is ApiResult.Success) {
            return ApiResult.Success(localResult.data.asChapterModel())
        }

        // 2. Check Connectivity
        if (!networkMonitor.isConnected()) {
            return ApiResult.Error("No internet connection")
        }

        // 3. Fetch from Remote
        return fetchChapterRemote(categoryId,levelId)
    }

    private suspend fun fetchChapterRemote(subjectId: Int, levelId: Int): ApiResult<ChapterModel> {
        return try {
            when (val response = networkRepository.fetchChapterDetails(subjectId,levelId)) {
                is ApiResult.Success -> {
                    if (response.data.status) {
                        val model = response.data.chapter.asChapterEntity().asChapterModel()
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