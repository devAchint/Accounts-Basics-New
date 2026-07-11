package com.techuntried.accountsbasics2.domain.repository

import com.techuntried.accountsbasics2.domain.model.BaseApiResponse
import com.techuntried.accountsbasics2.domain.model.UserFcmTokenRequest
import com.techuntried.accountsbasics2.domain.model.account.CreateGuestAccountRequest
import com.techuntried.accountsbasics2.domain.model.account.CreateGuestResponse
import com.techuntried.accountsbasics2.domain.model.account.UserAppVersionRequest
import com.techuntried.accountsbasics2.domain.model.analytics.LogEventRequest
import com.techuntried.accountsbasics2.domain.model.appConfig.FetchAppConfigResponse
import com.techuntried.accountsbasics2.domain.model.appUpdate.FetchAppUpdateInfoResponse
import com.techuntried.accountsbasics2.domain.model.subjects.FetchSubjectsResponse
import com.techuntried.accountsbasics2.domain.model.subjects.FetchSubjectResponse
import com.techuntried.accountsbasics2.domain.model.course.FetchCoursesResponse
import com.techuntried.accountsbasics2.domain.model.feedback.UploadFeedbackRequest
import com.techuntried.accountsbasics2.domain.model.level.FetchChapterResponse
import com.techuntried.accountsbasics2.domain.model.level.FetchChaptersResponse
import com.techuntried.accountsbasics2.domain.model.question.FetchQuestionsResponse
import com.techuntried.accountsbasics2.utils.ApiResult

interface NetworkRepository {

    suspend fun createGuestAccount(createGuestAccountRequest: CreateGuestAccountRequest): ApiResult<CreateGuestResponse>
    suspend fun updateFcmToken(userFcmTokenRequest: UserFcmTokenRequest): ApiResult<BaseApiResponse>
    suspend fun updateAppVersion(userAppVersionRequest: UserAppVersionRequest): ApiResult<BaseApiResponse>
    suspend fun fetchCourses(): ApiResult<FetchCoursesResponse>
    suspend fun fetchSubjects(): ApiResult<FetchSubjectsResponse>
    suspend fun fetchCategoryDetails(categoryId: Int): ApiResult<FetchSubjectResponse>
    suspend fun fetchCategoriesByGrade(grade:Int): ApiResult<FetchSubjectsResponse>

    suspend fun fetchLevelsByCategory(categoryId: Int): ApiResult<FetchChaptersResponse>
    suspend fun fetchLevelsDetails(categoryId: Int, levelId:Int): ApiResult<FetchChapterResponse>

    suspend fun fetchQuestionsByLevel(
        categoryId: Int,
        levelId: Int
    ): ApiResult<FetchQuestionsResponse>

    suspend fun fetchCategoryLastUpdatedDate(): ApiResult<BaseApiResponse>
    suspend fun fetchLevelLastUpdatedDate(): ApiResult<BaseApiResponse>
    suspend fun fetchQuestionLastUpdatedDate(): ApiResult<BaseApiResponse>

    suspend fun uploadFeedback(feedbackRequest: UploadFeedbackRequest): ApiResult<BaseApiResponse>

    suspend fun logEvent(logEventRequest: LogEventRequest): ApiResult<BaseApiResponse>

    suspend fun fetchAppConfig(): ApiResult<FetchAppConfigResponse>
    suspend fun fetchAppUpdateInfo(): ApiResult<FetchAppUpdateInfoResponse>
//    suspend fun fetchAds(): ApiResult<FetchAdsResponse>
//    suspend fun adClick(id:Int): ApiResult<BaseApiResponse>

}


