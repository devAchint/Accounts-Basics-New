package com.techuntried.accountsbasics2.domain.model.account

import kotlinx.serialization.Serializable

@Serializable
data class CreateGuestAccountRequest(
    val country: String,
    val uid: String,
    val age:String?,
    val fcmToken: String?,
    val appVersion:String
)