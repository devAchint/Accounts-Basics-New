package com.techuntried.accountsbasics2.domain.model.ads

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchAdsResponse(
    @SerialName("data")
    val ads: List<AdModel>,
    val message: String,
    val status: Boolean
)

@Serializable
data class AdModel(
    val id: Int,
    val adImageUrl: String,
    val adType: String,
    val clickUrl: String,
    val clicksCount: Int,
    val isRewarded: Boolean,
    val placement: String,
    val reward: Int,
    val status: String
)