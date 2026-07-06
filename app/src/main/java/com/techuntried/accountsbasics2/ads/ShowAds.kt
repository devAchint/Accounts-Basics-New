package com.techuntried.accountsbasics2.ads

import android.app.Activity
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdPreloader
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdPreloader
import com.techuntried.accountsbasics2.MyApplication
import com.techuntried.accountsbasics2.usecases.LogEventType

fun showInterstitialAd(
    adUnit: String,
    activity: Activity,
    logEvent: (LogEventType) -> Unit,
    onAdShown: () -> Unit
) {
    try {
        if (!canShowFullscreenAd(activity)) return
        var ad = InterstitialAdPreloader.pollAd(adUnit)

        if (ad == null) {
            val errorMsg = "Ad is not ready yet."
            logEvent(LogEventType.AdFailedToLoad(adType = "Interstitial", errorMessage = errorMsg))
            return
        }

        ad.adEventCallback =
            object : InterstitialAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    MyApplication.isFullscreenAdShowing = false
                    ad = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent(LogEventType.AdImpression("Interstitial"))
                }

                override fun onAdFailedToShowFullScreenContent(
                    fullScreenContentError: FullScreenContentError
                ) {
                    // Interstitial ad failed to show.
                    MyApplication.isFullscreenAdShowing = false
                    ad = null
                    logEvent(
                        LogEventType.AdFailedToShow(
                            adType = "Interstitial",
                            errorMessage = fullScreenContentError.message
                        )
                    )
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    MyApplication.isFullscreenAdShowing = true
                    onAdShown()
                }
            }

        ad?.show(activity)
    } catch (e: Exception) {
        logEvent(
            LogEventType.AdFailedToShow(
                adType = "Interstitial",
                errorMessage = e.message ?: "Unknown error"
            )
        )
    }

}


fun showRewardedAd(
    adUnit: String,
    activity: Activity,
    onRewardEarned: () -> Unit = {},
    onAdDismissed: () -> Unit = {},
    onAdFailed: (errorMessage: String) -> Unit,
    logEvent: (LogEventType) -> Unit,
) {
    try {
        var ad = RewardedAdPreloader.pollAd(adUnit)
        var rewardEarned = false

        if (ad == null) {
            val errorMsg = "Ad is not ready yet. Please try again."
            logEvent(LogEventType.AdFailedToLoad(adType = "Rewarded", errorMessage = errorMsg))
            onAdFailed(errorMsg)
            return // 👈 Exit immediately so we don't run the rest of the code
        }

        ad.adEventCallback =
            object : RewardedAdEventCallback {
                override fun onAdShowedFullScreenContent() {
                    MyApplication.isFullscreenAdShowing = true
                }

                override fun onAdDismissedFullScreenContent() {
                    // Interstitial ad did dismiss.
                    MyApplication.isFullscreenAdShowing = false
                    ad = null
                    if (rewardEarned) {
                        onAdDismissed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(
                    fullScreenContentError: FullScreenContentError
                ) {
                    MyApplication.isFullscreenAdShowing = false
                    ad = null
                    logEvent(
                        LogEventType.AdFailedToShow(
                            adType = "Rewarded",
                            errorMessage = fullScreenContentError.message
                        )
                    )
                }
            }

        ad?.show(activity) { rewardItem -> // User earned the reward.
            rewardEarned = true
            onRewardEarned()
        }

    } catch (e: Exception) {
        val errorMsg = e.message ?: "An unexpected error occurred."
        logEvent(LogEventType.AdFailedToShow(adType = "Rewarded", errorMessage = errorMsg))
        onAdFailed(errorMsg)
    }
}

fun canShowFullscreenAd(activity: Activity?): Boolean {

    if (activity == null) return false

    if (!MyApplication.isAppForeground) return false

    if (activity.isDestroyed) return false

    if (activity.isFinishing) return false

//    if (!activity.hasWindowFocus()) return false

    return true
}