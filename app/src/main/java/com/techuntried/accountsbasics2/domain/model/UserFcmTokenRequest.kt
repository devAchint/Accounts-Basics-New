package com.techuntried.accountsbasics2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserFcmTokenRequest(
    val fcmToken: String,
    val userId:Int
)