package com.techuntried.accountsbasics2.ads

import android.util.Log
import android.view.View
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.techuntried.accountsbasics2.databinding.NativeAdLargeBinding
import com.techuntried.accountsbasics2.usecases.LogEventType
import java.util.Date

object NativeAdManager {
    const val TAG = "MYDEBUG"
    private var nativeAd: NativeAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false

    private var loadTime: Long = 0
    private var adUnit: String? = null

    private var skipNextCorrect = false

    fun shouldShowNativeForCorrect(): Boolean {
        return if (skipNextCorrect) {
            skipNextCorrect = false
            false
        } else {
            skipNextCorrect = true
            true
        }
    }

    fun onWrongDialogShown() {
        skipNextCorrect = false
    }

    fun resetDialogShown() {
        skipNextCorrect = false
    }

    fun loadAd(
        adUnit: String,
        logEvent: (LogEventType) -> Unit
    ) {
        try {
            Log.d("MYDEBUG", "loading ad")
            this.adUnit = adUnit
            if (isLoadingAd || isAdAvailable()) {
                Log.d(TAG, "Native ad is either loading or has already loaded.")
                return
            }

            isLoadingAd = true

            val adTypes = listOf(NativeAd.NativeAdType.NATIVE)
            val adRequest = NativeAdRequest
                .Builder(adUnit, adTypes)
                .build()

            val adCallback =
                object : NativeAdLoaderCallback {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        this@NativeAdManager.nativeAd = nativeAd
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d(TAG, "Native ad loaded.")
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isLoadingAd = false
                        logEvent(
                            LogEventType.AdFailedToLoad(
                                adType = "Game Native",
                                errorMessage = adError.message
                            )
                        )
                        Log.w(TAG, "Native ad failed to load: $adError")
                    }
                }

            NativeAdLoader.load(adRequest, adCallback)
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
            logEvent(
                LogEventType.AdFailedToLoad(
                    adType = "Game Native",
                    errorMessage = e.message ?: "Unexpected SDK crash"
                )
            )
        }
    }


    fun displayLargeNativeAd(nativeAdBinding: NativeAdLargeBinding) {
        try {
            if (isShowingAd) {
                Log.d(TAG, "Native ad is already showing.")
//            onShowAdCompleteListener?.onShowAdComplete()
                return
            }

            isShowingAd = true

            // Set the native ad view elements.
            val nativeAdView = nativeAdBinding.root
//    nativeAdView.bodyView = nativeAdBinding.adBody
            nativeAdView.callToActionView = nativeAdBinding.adCallToAction
            nativeAdView.headlineView = nativeAdBinding.adHeadline
            nativeAdView.bodyView = nativeAdBinding.adBody
            nativeAdView.iconView = nativeAdBinding.adAppIcon
//    nativeAdView.priceView = nativeAdBinding.adPrice
//    nativeAdView.starRatingView = nativeAdBinding.adStars
//    nativeAdView.storeView = nativeAdBinding.adStore

            // Set the view element with the native ad assets.
//    nativeAdBinding.adBody.text = nativeAd.body
            nativeAdBinding.adCallToAction.text = nativeAd?.callToAction
            nativeAdBinding.adHeadline.text = nativeAd?.headline
            nativeAdBinding.adBody.text = nativeAd?.body
            nativeAdBinding.adAppIcon.setImageDrawable(nativeAd?.icon?.drawable)
//    nativeAdBinding.adPrice.text = nativeAd.price
//    nativeAd.starRating?.toFloat().let { value ->
//        nativeAdBinding.adStars.rating = value
//    }
//    nativeAdBinding.adStore.text = nativeAd.store

            // Hide views for assets that don't have data.
//    nativeAdBinding.adBody.visibility = getAssetViewVisibility(nativeAd.body)
            nativeAdBinding.adCallToAction.visibility =
                getAssetViewVisibility(nativeAd?.callToAction)
            nativeAdBinding.adHeadline.visibility = getAssetViewVisibility(nativeAd?.headline)
            nativeAdBinding.adBody.visibility = getAssetViewVisibility(nativeAd?.body)
            nativeAdBinding.adAppIcon.visibility = getAssetViewVisibility(nativeAd?.icon)
//    nativeAdBinding.adPrice.visibility = getAssetViewVisibility(nativeAd.price)
//    nativeAdBinding.adStars.visibility = getAssetViewVisibility(nativeAd.starRating)
//    nativeAdBinding.adStore.visibility = getAssetViewVisibility(nativeAd.store)

            // Inform GMA Next-Gen SDK that you have finished populating
            // the native ad views with this native ad.
            nativeAd?.let {
                nativeAdView.registerNativeAd(it, null)
            }
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
        }
    }

    fun destroyCurrentAd() {
        nativeAd?.destroy()
        nativeAd = null
        isShowingAd = false
    }

    private fun getAssetViewVisibility(asset: Any?): Int {
        return if (asset == null) View.INVISIBLE else View.VISIBLE
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    fun isAdAvailable(): Boolean {
        return nativeAd != null && wasLoadTimeLessThanNHoursAgo(1)
    }
}