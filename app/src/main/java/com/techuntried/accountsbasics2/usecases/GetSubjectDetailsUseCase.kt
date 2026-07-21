package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.mappers.asSubjectEntity
import com.techuntried.accountsbasics2.data.mappers.asSubjectModel
import com.techuntried.accountsbasics2.data.repository.RoomRepository
import com.techuntried.accountsbasics2.domain.model.subjects.SubjectModel
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class GetSubjectDetailsUseCase @Inject constructor(
    private val roomRepository: RoomRepository,
    private val networkRepository: NetworkRepository,
    private val networkMonitor: NetworkMonitor // Better than passing 'context'
) {
    suspend operator fun invoke(subjectId: Int): ApiResult<SubjectModel> {
        // 1. Check Local Source first
        val localResult = roomRepository.fetchSubjectById(subjectId)
        if (localResult is ApiResult.Success) {
            return ApiResult.Success(localResult.data.asSubjectModel())
        }

        // 2. Check Connectivity
        if (!networkMonitor.isConnected()) {
            return ApiResult.Error("No internet connection")
        }

        // 3. Fetch from Remote
        return fetchSubjectRemote(subjectId)
    }

    private suspend fun fetchSubjectRemote(categoryId: Int): ApiResult<SubjectModel> {
        return try {
            when (val response = networkRepository.fetchSubjectDetails(categoryId)) {
                is ApiResult.Success -> {
                    if (response.data.status) {
                        val model = response.data.subject.asSubjectEntity().asSubjectModel()
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