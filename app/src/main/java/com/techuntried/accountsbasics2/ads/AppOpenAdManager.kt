package com.techuntried.accountsbasics2.ads

import android.app.Activity
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.techuntried.accountsbasics2.MyApplication
import com.techuntried.accountsbasics2.usecases.LogEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

object AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    private var loadTime: Long = 0
    const val TAG = "MYDEBUG"
    private var adUnit: String? = null

    private var coroutineScope: CoroutineScope? = null
    private var onAdError: (suspend (LogEventType) -> Unit)? = null

    private fun fireAdError(type: LogEventType) {
        val scope = coroutineScope ?: return
        val logger = onAdError ?: return

        scope.launch {

            try {
                logger(type)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to log ad error", e)
            }
        }
    }

    fun init(
        scope: CoroutineScope,
        onAdError: suspend (LogEventType) -> Unit
    ) {
        this.coroutineScope = scope
        this.onAdError = onAdError
    }

    fun loadAd(adUnit: String) {
        try {
            this.adUnit = adUnit
            if (isLoadingAd || isAdAvailable()) {
                Log.d(TAG, "App open ad is either loading or has already loaded.")
                return
            }

            isLoadingAd = true
            AppOpenAd.load(
                AdRequest.Builder(adUnit).build(),
                object : AdLoadCallback<AppOpenAd> {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        // Called when an ad has loaded.
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d(TAG, "App open ad loaded.")
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isLoadingAd = false
                        Log.w(TAG, "App open ad failed to load: $adError")
                        fireAdError(LogEventType.AdFailedToLoad("App Open", adError.message))
                    }
                },
            )
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            fireAdError(
                LogEventType.AdFailedToLoad(
                    "App Open",
                    e.message ?: "Unexpected SDK crash"
                )
            )
        }
    }

    fun showAdIfAvailable(activity: Activity) {
        try {
            // If the app open ad is not available yet, invoke the callback.
            if (!isAdAvailable()) {
                Log.d(TAG, "App open ad is not ready yet.")
                return
            }

            if (isShowingAd) {
                Log.d(TAG, "App open ad is already showing.")
                return
            }

            if (MyApplication.isFullscreenAdShowing) {
                Log.d("MYDEBUG", "Cant show full screen ad")
                return
            }

            if (!canShowFullscreenAd(activity)) {
                Log.d("MYDEBUG", "Cant show full screen ad")
                return
            }


            appOpenAd?.adEventCallback =
                object : AppOpenAdEventCallback {
                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "App open ad showed.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "App open ad dismissed.")
                        appOpenAd = null
                        isShowingAd = false
                        adUnit?.let {
                            loadAd(it)
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(
                        fullScreenContentError: FullScreenContentError
                    ) {
                        appOpenAd = null
                        isShowingAd = false
                        Log.w(TAG, "App open ad failed to show: $fullScreenContentError")
                        adUnit?.let {
                            loadAd(it)
                        }
                        fireAdError(
                            LogEventType.AdFailedToShow(
                                "App Open",
                                fullScreenContentError.message
                            )
                        )

                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "App open ad recorded an impression.")
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "App open ad recorded a click.")
                    }
                }

            isShowingAd = true
            appOpenAd?.show(activity)
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            fireAdError(
                LogEventType.AdFailedToShow(
                    "App Open",
                    e.message ?: "Unexpected SDK crash"
                )
            )
        }
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }
}