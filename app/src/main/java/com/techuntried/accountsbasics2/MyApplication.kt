package com.techuntried.accountsbasics2

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.techuntried.accountsbasics2.ads.AppOpenAdManager
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    private var currentActivity: Activity? = null
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        var isAppForeground = false

        @Volatile
        var isFullscreenAdShowing = false
    }

    @Inject
    lateinit var logEventUseCase: LogEventUseCase

    override fun onCreate() {
        super<Application>.onCreate()

        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        AppOpenAdManager.init(
            scope = appScope,
            onAdError = {
                logEventUseCase(it)
            }
        )
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        if (currentActivity === p0) {
            currentActivity = null
        }
    }

    override fun onActivityPaused(p0: Activity) {
        isAppForeground = false
    }


    override fun onActivityResumed(p0: Activity) {
        isAppForeground = true
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isAppForeground = true
        currentActivity?.let { activity ->
            AppOpenAdManager.showAdIfAvailable(activity)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        isAppForeground = false
    }

}