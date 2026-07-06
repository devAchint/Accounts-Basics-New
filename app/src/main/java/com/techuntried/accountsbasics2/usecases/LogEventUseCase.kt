package com.techuntried.accountsbasics2.usecases

import android.util.Log
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.domain.model.analytics.LogEventRequest
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

sealed interface LogEventType {
    data class LevelStart(val categoryId: Int, val levelId: Int) : LogEventType
    data class LevelComplete(
        val categoryId: Int,
        val levelId: Int,
        val answeredCount: Int,
        val totalQuestions: Int
    ) : LogEventType

    data class ScreenVisit(val screenName: String) : LogEventType

    data class FeatureError(val featureName: String, val errorMessage: String) : LogEventType
    data class AdFailedToShow(val adType: String, val errorMessage: String? = null) : LogEventType
    data class AdFailedToLoad(val adType: String, val errorMessage: String? = null) : LogEventType
    data class AdImpression(val adType: String) : LogEventType
    data object ReviewFlowLaunched : LogEventType
}

fun LogEventType.eventName(): String {
    return when (this) {
        is LogEventType.LevelComplete -> "category_complete"
        is LogEventType.LevelStart -> "category_start"
        is LogEventType.ScreenVisit -> "screen_view"
        is LogEventType.FeatureError -> "feature_error"
        is LogEventType.AdFailedToShow -> "ad_failed_to_show"
        is LogEventType.AdFailedToLoad -> "ad_failed_to_load"
        LogEventType.ReviewFlowLaunched -> "review_flow_launched"
        is LogEventType.AdImpression -> "ad_impression"
    }
}

fun LogEventType.payload(): JsonObject {
    return when (this) {
        is LogEventType.LevelComplete -> buildJsonObject {
            put("categoryId", categoryId)
            put("levelId", levelId)
            put("answeredCount", answeredCount)
            put("totalQuestions", totalQuestions)
        }

        is LogEventType.LevelStart -> buildJsonObject {
            put("categoryId", categoryId)
            put("levelId", levelId)
        }

        is LogEventType.ScreenVisit -> buildJsonObject {
            put("screenName", screenName)
        }
        

        is LogEventType.FeatureError -> buildJsonObject {
            put("featureName", featureName)
            put("errorMessage", errorMessage)
        }

        is LogEventType.AdFailedToShow -> buildJsonObject {
            put("adType", adType)
            put("errorMessage", errorMessage)
        }

        is LogEventType.AdFailedToLoad -> buildJsonObject {
            put("adType", adType)
            put("errorMessage", errorMessage)
        }

        LogEventType.ReviewFlowLaunched -> buildJsonObject { }
        is LogEventType.AdImpression -> buildJsonObject {
            put("adType", adType)
        }
    }
}

class LogEventUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(logEventType: LogEventType) {
        try {

            val userId = dataStoreRepository.getUserProfile()?.userId ?: 0

            val logEventRequest = LogEventRequest(
                userId = userId,
                eventName = logEventType.eventName(),
                payload = logEventType.payload()
            )
            //remove before publishing
            return
            when (val response = networkRepository.logEvent(logEventRequest)) {
                is ApiResult.Success -> {
                    Log.d("MYDEBUG", "Event logged successfully")
                }

                is ApiResult.Error -> {
                    Log.d("MYDEBUG", "Event logging failed: ${response.errorMessage}")
                }
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
}