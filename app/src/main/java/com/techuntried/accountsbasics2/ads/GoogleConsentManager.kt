package com.techuntried.accountsbasics2.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleConsentManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val consentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    private val _canRequestAds = MutableStateFlow(false)
    val canRequestAds = _canRequestAds.asStateFlow()

    fun privacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    fun gatherConsent(
        activity: Activity,
        onComplete: (FormError?) -> Unit
    ) {

//        val debugSettings =
//            ConsentDebugSettings.Builder(activity)
//                .setDebugGeography(
//                    ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
//                )
//                .addTestDeviceHashedId("E865DB5F2CEF55CD1100769F17892F4E")
//                .build()

        val params =
            ConsentRequestParameters.Builder()
//                .setConsentDebugSettings(debugSettings)
                .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { error ->

                    _canRequestAds.value =
                        consentInformation.canRequestAds()

                    onComplete(error)
                }
            },
            { error ->

                _canRequestAds.value =
                    consentInformation.canRequestAds()

                onComplete(error)
            }
        )

        _canRequestAds.value = consentInformation.canRequestAds()
    }
}