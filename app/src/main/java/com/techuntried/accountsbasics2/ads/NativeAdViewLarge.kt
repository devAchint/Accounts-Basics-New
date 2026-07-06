package com.techuntried.accountsbasics2.ads

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.techuntried.accountsbasics2.databinding.NativeAdLargeBinding
import com.techuntried.accountsbasics2.databinding.NativeAdLayout3Binding
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.findActivity


@Composable
fun NativeAdViewLarge(
    modifier: Modifier = Modifier,
    adUnit: String?,
    logEvent: (LogEventType) -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val shouldShowLargeNative = remember {
        NativeAdManager.shouldShowNativeForCorrect()
    }

    if (activity == null || adUnit == null || !NativeAdManager.isAdAvailable() || !shouldShowLargeNative) return

    DisposableEffect(Unit) {
        onDispose {
            NativeAdManager.destroyCurrentAd()
            NativeAdManager.loadAd(
                adUnit = adUnit,
                logEvent = logEvent
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FrameLayout(ctx).apply {
                val view = NativeAdLargeBinding.inflate(activity.layoutInflater)
                val adView = view.root
                NativeAdManager.displayLargeNativeAd(view)
                removeAllViews()
                addView(adView)
            }
        }
    )
}

@Composable
fun NativeAdViewSmall(
    modifier: Modifier = Modifier,
    adUnit: String?,
    logEvent: (LogEventType) -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    if (activity == null || adUnit == null) return

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FrameLayout(ctx).apply {

                // Inflate immediately
                val binding =
                    NativeAdLayout3Binding.inflate(activity.layoutInflater)

                // Placeholder state
                binding.adHeadline.text = "Sponsored"
                binding.adBody.text = ""
                binding.adCallToAction.text = "Loading..."
                binding.adIconCard.visibility = View.GONE

                addView(binding.root)

                // Then load ad
                showSmallNativeAd(
                    activity = activity,
                    adUnit = adUnit,
                    logEvent = logEvent,
                    frameLayout = this,
                    binding = binding
                )
            }
        }
    )
}

private fun showSmallNativeAd(
    adUnit: String,
    logEvent: (LogEventType) -> Unit,
    frameLayout: FrameLayout,
    binding: NativeAdLayout3Binding,
    activity: Activity
) {
    try {
        val adTypes = listOf(NativeAd.NativeAdType.NATIVE)
        val adRequest = NativeAdRequest
            .Builder(adUnit, adTypes)
            .build()

        val adCallback =
            object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    activity.runOnUiThread {
                        displaySmallNativeAd(binding, nativeAd)
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logEvent(
                        LogEventType.AdFailedToLoad(
                            adType = "Native",
                            errorMessage = adError.message
                        )
                    )
                    Log.w("MYDEBUG", "Native ad failed to load: $adError")
                    activity.runOnUiThread {
                        frameLayout.removeAllViews()
                        frameLayout.visibility = View.GONE
                    }
                }
            }

        NativeAdLoader.load(adRequest, adCallback)
    } catch (e: Exception) {
        Log.d("MYDEBUG", "${e.message}")
        logEvent(
            LogEventType.AdFailedToLoad(
                adType = "Native",
                errorMessage = e.message ?: "Unexpected SDK crash"
            )
        )
    }
}

private fun displaySmallNativeAd(nativeAdBinding: NativeAdLayout3Binding, nativeAd: NativeAd) {
    try {
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
        nativeAdBinding.adCallToAction.text = nativeAd.callToAction
        nativeAdBinding.adHeadline.text = nativeAd.headline
        nativeAdBinding.adBody.text = nativeAd.body
        nativeAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
//    nativeAdBinding.adPrice.text = nativeAd.price
//    nativeAd.starRating?.toFloat().let { value ->
//        nativeAdBinding.adStars.rating = value
//    }
//    nativeAdBinding.adStore.text = nativeAd.store

        // Hide views for assets that don't have data.
//    nativeAdBinding.adBody.visibility = getAssetViewVisibility(nativeAd.body)

        nativeAdBinding.adCallToAction.visibility =
            getAssetViewVisibility(nativeAd.callToAction)
        nativeAdBinding.adHeadline.visibility =
            getAssetViewVisibility(nativeAd.headline)
        nativeAdBinding.adBody.visibility = getAssetViewVisibility(nativeAd.body)
        nativeAdBinding.adAppIcon.visibility = getAssetViewVisibility(nativeAd.icon)
        nativeAdBinding.adIconCard.visibility = getAssetViewVisibility(nativeAd.icon)
//    nativeAdBinding.adPrice.visibility = getAssetViewVisibility(nativeAd.price)
//    nativeAdBinding.adStars.visibility = getAssetViewVisibility(nativeAd.starRating)
//    nativeAdBinding.adStore.visibility = getAssetViewVisibility(nativeAd.store)

        // Inform GMA Next-Gen SDK that you have finished populating
        // the native ad views with this native ad.
        nativeAdView.registerNativeAd(nativeAd, null)
    } catch (e: Exception) {
        Log.d("MYDEBUG", "${e.message}")
    }
}

private fun getAssetViewVisibility(asset: Any?): Int {
    return if (asset == null) View.INVISIBLE else View.VISIBLE
}