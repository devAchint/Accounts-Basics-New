package com.techuntried.accountsbasics2.domain.model.account

import com.techuntried.accountsbasics2.domain.model.user.UserModel
import kotlinx.serialization.Serializable

@Serializable
data class CreateGuestResponse(
    val message: String,
    val status: Boolean,
    val user: UserModel
)