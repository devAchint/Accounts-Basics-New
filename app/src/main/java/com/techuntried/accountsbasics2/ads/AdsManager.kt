package com.techuntried.accountsbasics2.ads

import android.content.Context
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private var initialized = false

    fun initialize(onComplete: () -> Unit = {}) {
        if (initialized) {
            onComplete()
            return
        }
        //change before publishing
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(
                context,
                InitializationConfig.Builder("ca-app-pub-3940256099942544~3347511713").build()
            ) {}
            initialized = true
            onComplete()
        }
    }
}