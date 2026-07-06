package com.techuntried.accountsbasics2.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val country: String,
    val userId: Int,
    val isGuest: Boolean,
    val name: String,
    val appVersion: String? = null
)