package com.techuntried.accountsbasics2.ads

import android.app.Activity
import android.os.Build
import android.view.WindowMetrics
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.utils.findActivity
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun BannerAdCard(
    modifier: Modifier = Modifier,
    bannerAdUnit: String,
    logEvent:(LogEventType)->Unit,
) {
    var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }
    val context = LocalContext.current
    val activity = context.findActivity() ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
    ) {
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = BorderColor
        )
        bannerAdState?.let { bannerAd ->
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = { ctx ->
                    bannerAd.getView(activity)
                }
            )
        }

    }

    val adSize = adSize(activity)
    val isPreviewMode = LocalInspectionMode.current


    LaunchedEffect(bannerAdUnit) {
        try {
            bannerAdState?.destroy()
            if (!isPreviewMode) {

                when (val result = BannerAd.load(BannerAdRequest.Builder(adUnitId = bannerAdUnit, adSize = adSize).build())) {
                    is AdLoadResult.Success -> {
                        bannerAdState = result.ad
                    }

                    is AdLoadResult.Failure -> {
                        logEvent(LogEventType.AdFailedToLoad(adType = "Banner", errorMessage = result.error.message))
                    }
                }
            }
        }catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
            logEvent(
                LogEventType.AdFailedToShow(
                    adType = "Banner",
                    errorMessage = e.message ?: "Unexpected SDK crash"
                )
            )
        }

    }

    DisposableEffect(Unit) {
        onDispose {
            bannerAdState?.destroy()
        }
    }

}

private fun adSize(activity: Activity): AdSize {
    val displayMetrics = activity.resources.displayMetrics
    val adWidthPixels =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            displayMetrics.widthPixels
        }
    val density = displayMetrics.density
    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getInlineAdaptiveBannerAdSize(adWidth, 60)
}