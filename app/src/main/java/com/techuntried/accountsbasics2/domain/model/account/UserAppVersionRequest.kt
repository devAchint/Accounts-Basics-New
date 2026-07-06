package com.techuntried.accountsbasics2.domain.model.account

import kotlinx.serialization.Serializable

@Serializable
data class UserAppVersionRequest(
    val appVersion: String,
    val userId:Int
)