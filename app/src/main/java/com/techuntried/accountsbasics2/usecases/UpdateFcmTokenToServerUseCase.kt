package com.techuntried.accountsbasics2.usecases


import android.util.Log
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.domain.model.UserFcmTokenRequest
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import javax.inject.Inject

class UpdateFcmTokenToServerUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(fcmToken: String) {
        try {
            val userId = dataStoreRepository.getUserProfile()?.userId ?: return
            val request = UserFcmTokenRequest(
                fcmToken = fcmToken,
                userId = userId
            )
            val response = networkRepository.updateFcmToken(request)
            if (response is ApiResult.Error){
                Log.d("MYDEBUG", response.errorMessage)
            }

        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
        }
    }
}