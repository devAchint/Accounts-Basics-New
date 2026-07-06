package com.techuntried.accountsbasics2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseApiResponse(
    val status: Boolean,
    val message: String
)
