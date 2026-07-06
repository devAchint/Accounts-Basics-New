package com.techuntried.accountsbasics2.domain.model.level

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchLevelsResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val levels: List<LevelApiResponse>
)

@Serializable
data class FetchLevelResponse(
    val status: Boolean,
    val message: String,
    @SerialName("data")
    val level: LevelApiResponse
)

@Serializable
data class LevelApiResponse(
    val categoryId: Int,
    val levelId: Int,
    val levelName: String,
    val questions: Int,
    val topic:String?
)