package com.techuntried.accountsbasics2.usecases

import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.DeviceInfoProvider
import com.techuntried.accountsbasics2.domain.model.feedback.UploadFeedbackRequest
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import com.techuntried.accountsbasics2.utils.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class UploadSuggestionWithLimitUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) {

    suspend operator fun invoke(comment:String): ApiResult<String> {
        val lastFeedbackDate = dataStoreRepository.getLastFeedbackDate()
        val feedbackCounts = dataStoreRepository.getFeedbacksCount()
        val userId = dataStoreRepository.getUserProfile()?.userId

        val feedbackRequest = UploadFeedbackRequest(
            comment = comment,
            sender = deviceInfoProvider.getSenderInfo().toString(),
            userId = userId
        )
        val canFeedback = isCanFeedback(lastFeedbackDate, feedbackCounts) {
            dataStoreRepository.resetFeedbackCount()
        }

        if (!canFeedback) {
            return ApiResult.Error("You've reached the daily limit. Please try again tomorrow.")
        }

        return when (val response = networkRepository.uploadFeedback(feedbackRequest)) {
            is ApiResult.Error -> ApiResult.Error(response.errorMessage)
            is ApiResult.Success -> {
                if (response.data.status) {
                    dataStoreRepository.saveLastFeedbackDate(Date().formatDate())
                    dataStoreRepository.incrementFeedbacksCount()
                    ApiResult.Success("Your suggestion has been recorded")
                } else {
                    ApiResult.Error(response.data.message)
                }
            }
        }
    }

    private suspend fun isCanFeedback(
        lastFeedbackDate: String?,
        feedbackCount: Int,
        onResetFeedbackCount: suspend () -> Unit
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val currentDate = Date().formatDate()
            if (lastFeedbackDate == currentDate) {
                feedbackCount < 6
            } else {
                onResetFeedbackCount()
                true
            }
        }
    }
}
