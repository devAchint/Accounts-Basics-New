package com.techuntried.accountsbasics2.domain.model.appConfig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchAppConfigResponse(
    @SerialName("data")
    val appConfig: AppConfigResponse,
    val message: String?,
    val status: Boolean
)

@Serializable
data class AppConfigResponse(
    val scoreRatingEnabled: Boolean,
    val levelRatingEnabled: Boolean,
    val adsEnabled: Boolean,
    val scoreNativeAdEnabled: Boolean,
    val interstitialAdUnit: String,
    val rewardedAdUnit: String,
    val bannerAdUnit: String,
    val scoreNativeAdUnit: String,
    val gameNativeAdUnit:String,
    val appOpenAdUnit:String,
    val interstitialAdClicks: Int,
    val unlockLevelCoins:Int,
    val bombCoins:Int,
    val addTimeCoins:Int,
    val tryAgainCoins:Int,
    val correctAnswerCoins:Int,
)