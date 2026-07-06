package com.techuntried.accountsbasics2.domain.model.analytics

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LogEventRequest(
    val userId: Int,
    val eventName: String,
    val payload: JsonObject
)