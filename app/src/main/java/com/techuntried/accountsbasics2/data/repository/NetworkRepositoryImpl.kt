package com.techuntried.accountsbasics2.data.repository

import android.content.Context
import com.techuntried.accountsbasics2.domain.model.BaseApiResponse
import com.techuntried.accountsbasics2.domain.model.UserFcmTokenRequest
import com.techuntried.accountsbasics2.domain.model.account.CreateGuestAccountRequest
import com.techuntried.accountsbasics2.domain.model.account.CreateGuestResponse
import com.techuntried.accountsbasics2.domain.model.account.UserAppVersionRequest
import com.techuntried.accountsbasics2.domain.model.analytics.LogEventRequest
import com.techuntried.accountsbasics2.domain.model.appConfig.FetchAppConfigResponse
import com.techuntried.accountsbasics2.domain.model.appUpdate.FetchAppUpdateInfoResponse
import com.techuntried.accountsbasics2.domain.model.category.FetchCategoriesResponse
import com.techuntried.accountsbasics2.domain.model.category.FetchCategoryResponse
import com.techuntried.accountsbasics2.domain.model.course.CourseResponse
import com.techuntried.accountsbasics2.domain.model.course.FetchCoursesResponse
import com.techuntried.accountsbasics2.domain.model.feedback.UploadFeedbackRequest
import com.techuntried.accountsbasics2.domain.model.level.FetchLevelResponse
import com.techuntried.accountsbasics2.domain.model.level.FetchLevelsResponse
import com.techuntried.accountsbasics2.domain.model.question.FetchQuestionsResponse
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import com.techuntried.accountsbasics2.utils.getApiResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject


class NetworkRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val client: HttpClient
) : NetworkRepository {

    private val json = Json {}

    override suspend fun createGuestAccount(createGuestAccountRequest: CreateGuestAccountRequest): ApiResult<CreateGuestResponse> {
        return getApiResponse {
            client.post("api/guest_login") {
                contentType(ContentType.Application.Json)
                setBody(createGuestAccountRequest)
            }.body<CreateGuestResponse>()
        }
    }

    override suspend fun updateFcmToken(userFcmTokenRequest: UserFcmTokenRequest): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.post("api/updateFcmToken") {
                contentType(ContentType.Application.Json)
                setBody(userFcmTokenRequest)
            }.body<BaseApiResponse>()
        }
    }

    override suspend fun updateAppVersion(userAppVersionRequest: UserAppVersionRequest): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.post("api/updateAppVersion") {
                contentType(ContentType.Application.Json)
                setBody(userAppVersionRequest)
            }.body<BaseApiResponse>()
        }
    }

    override suspend fun fetchCourses(): ApiResult<FetchCoursesResponse> {
        val text = context.assets
            .open("courses.json")
            .bufferedReader()
            .use { it.readText() }
        val courses = json.decodeFromString<List<CourseResponse>>(text)
        val response = FetchCoursesResponse(data = courses, status = true, message = "")
        return ApiResult.Success(response)
    }

    override suspend fun fetchCategories(): ApiResult<FetchCategoriesResponse> {
        return getApiResponse {
            client.get("api/categories").body<FetchCategoriesResponse>()
        }
    }

    override suspend fun fetchCategoryDetails(categoryId: Int): ApiResult<FetchCategoryResponse> {
        return getApiResponse {
            client.get("api/categories/$categoryId").body<FetchCategoryResponse>()
        }
    }

    override suspend fun fetchCategoriesByGrade(grade: Int): ApiResult<FetchCategoriesResponse> {
        return getApiResponse {
            client.get("api/categoriesByGrade/$grade").body<FetchCategoriesResponse>()
        }
    }

    override suspend fun fetchLevelsByCategory(categoryId: Int): ApiResult<FetchLevelsResponse> {
        return getApiResponse {
            client.get("api/levelByCategory/category/$categoryId").body<FetchLevelsResponse>()
        }
    }

    override suspend fun fetchLevelsDetails(
        categoryId: Int,
        levelId: Int
    ): ApiResult<FetchLevelResponse> {
        return getApiResponse {
            client.get("api/levelDetails/$categoryId/$levelId").body<FetchLevelResponse>()
        }
    }

    override suspend fun fetchQuestionsByLevel(
        categoryId: Int,
        levelId: Int
    ): ApiResult<FetchQuestionsResponse> {
        return getApiResponse {
            client.get("api/questionsByLevel/category/$categoryId/level/$levelId")
                .body<FetchQuestionsResponse>()
        }
    }

    override suspend fun fetchCategoryLastUpdatedDate(): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.get("api/generals/key/categoryLastUpdatedDate").body<BaseApiResponse>()
        }
    }

    override suspend fun fetchLevelLastUpdatedDate(): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.get("api/generals/key/levelLastUpdatedDate").body<BaseApiResponse>()
        }
    }

    override suspend fun fetchQuestionLastUpdatedDate(): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.get("api/generals/key/questionLastUpdatedDate").body<BaseApiResponse>()
        }
    }

    override suspend fun uploadFeedback(feedbackRequest: UploadFeedbackRequest): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.post("api/feedbacks") {
                contentType(ContentType.Application.Json)
                setBody(feedbackRequest)
            }.body<BaseApiResponse>()
        }
    }

    override suspend fun logEvent(logEventRequest: LogEventRequest): ApiResult<BaseApiResponse> {
        return getApiResponse {
            client.post("api/analytics/logEvent") {
                contentType(ContentType.Application.Json)
                setBody(logEventRequest)
            }.body<BaseApiResponse>()
        }
    }


    override suspend fun fetchAppConfig(): ApiResult<FetchAppConfigResponse> {
        return getApiResponse {
            client.get("api/appConfig").body<FetchAppConfigResponse>()
        }
    }

    override suspend fun fetchAppUpdateInfo(): ApiResult<FetchAppUpdateInfoResponse> {
        return getApiResponse {
            client.get("api/appUpdateInfo").body<FetchAppUpdateInfoResponse>()
        }
    }

//    override suspend fun fetchAds(): ApiResult<FetchAdsResponse> {
//        return getApiResponse {
//            client.get("api/ads").body<FetchAdsResponse>()
//        }
//    }
//
//    override suspend fun adClick(id: Int): ApiResult<BaseApiResponse> {
//        return getApiResponse {
//            client.put("api/ads/increaseClicks/$id").body<BaseApiResponse>()
//        }
//    }
}