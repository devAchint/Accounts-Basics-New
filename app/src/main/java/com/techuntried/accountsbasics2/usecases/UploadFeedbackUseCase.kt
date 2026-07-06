package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.DeviceInfoProvider
import com.techuntried.accountsbasics2.domain.model.feedback.UploadFeedbackRequest
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class UploadFeedbackUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) {

    suspend operator fun invoke(comment: String): ApiResult<String> {
        val userId = dataStoreRepository.getUserProfile()?.userId

        val feedbackRequest = UploadFeedbackRequest(
            comment = comment,
            sender = deviceInfoProvider.getSenderInfo().toString(),
            userId = userId
        )

        return when (val response = networkRepository.uploadFeedback(feedbackRequest)) {
            is ApiResult.Error -> ApiResult.Error(response.errorMessage)
            is ApiResult.Success -> {
                if (response.data.status) {
                    ApiResult.Success("Your Feedback has been recorded")
                } else {
                    ApiResult.Error(response.data.message)
                }
            }
        }
    }
}