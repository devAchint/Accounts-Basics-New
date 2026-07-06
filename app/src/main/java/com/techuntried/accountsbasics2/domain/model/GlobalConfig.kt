package com.techuntried.accountsbasics2.domain.model

data class GlobalConfig(
    val scoreRatingEnabled: Boolean = false,
    val levelRatingEnabled: Boolean = false,
    val adsEnabled: Boolean = false,
    val testEnabled: Boolean = true,//change before publishing
    val scoreNativeAdEnabled: Boolean = false,
    var interCount: Int = 0,
    val interClicks: Int = 2,
    val interstitialAdUnit: String? = null,
    val appOpenAdUnit: String? = null,
    val gameNativeAdUnit: String? = null,
    val rewardedAdUnit: String? = null,
    val bannerAdUnit: String? = null,
    val scoreNativeAdUnit: String? = null
){
    fun shouldShowInterstitial(): Boolean {
        return ((interCount + 1) % interClicks == 0 && adsEnabled)
    }

    fun recordInterstitialShown() {
        interCount++
    }

    fun testOrRealAppOpenAdUnit(): String? {
        if (!adsEnabled) return null
        if (testEnabled) return "ca-app-pub-3940256099942544/9257395921"
        return appOpenAdUnit
    }

    fun testOrRealInterstitialAdUnit(): String? {
        if (!adsEnabled) return null
        if (testEnabled) return "ca-app-pub-3940256099942544/1033173712"
        return interstitialAdUnit
    }

    fun testOrRealRewardedAdUnit(): String? {
        if (!adsEnabled) return null
        if (testEnabled) return "ca-app-pub-3940256099942544/5224354917"
        return rewardedAdUnit
    }

    fun testOrRealNativeAdUnit(): String? {
        if (!adsEnabled || !scoreNativeAdEnabled) return null
        if (testEnabled) return "ca-app-pub-3940256099942544/2247696110"
        return scoreNativeAdUnit
    }

    fun testOrRealGameNativeAdUnit(): String? {
        if (!adsEnabled) return null
        if (testEnabled) return "ca-app-pub-3940256099942544/2247696110"
        return gameNativeAdUnit
    }

    fun testOrRealBannerAdUnit(): String? {
        if (!adsEnabled) return null
        if (testEnabled) return "ca-app-pub-3940256099942544/9214589741"
        return bannerAdUnit
    }

}
