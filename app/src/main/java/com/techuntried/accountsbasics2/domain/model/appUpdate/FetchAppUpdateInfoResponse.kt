package com.techuntried.accountsbasics2.domain.model.appUpdate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchAppUpdateInfoResponse(
    @SerialName("data")
    val updateInfo: AppUpdateInfoResponse,
    val message: String,
    val status: Boolean
)

@Serializable
data class AppUpdateInfoResponse(
    val versionCode: Int,
    val updateTitle: String,
    val updateBody: String
)