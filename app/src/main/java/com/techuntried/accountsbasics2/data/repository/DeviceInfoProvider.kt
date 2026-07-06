package com.techuntried.accountsbasics2.data.repository

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.to

@Singleton
class DeviceInfoProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val staticDeviceInfo: Map<String, String> by lazy {
        mapOf(
            "Device Name" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "Android Version" to "Android ${Build.VERSION.RELEASE}",
            "Screen Resolution" to context.resources.displayMetrics.let {
                "${it.widthPixels}x${it.heightPixels}"
            }
        )
    }

    fun getSenderInfo(): Map<String, String> {
        return try {
            val deviceInfo = staticDeviceInfo.toMutableMap()

            // Dynamic Info
            val locale = Locale.getDefault()
            deviceInfo["Language"] = "${locale.language}-${locale.country}"

            // WARNING: Still potentially slow; consider making this function 'suspend'
            // or returning a Flow/Deferred if IP is critical.
            deviceInfo["IP Address"] = getIPAddress()

            deviceInfo
        } catch (e: Exception) {
            mapOf("Error" to (e.message ?: "Unknown error"))
        }
    }

    fun getCountry():String{
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = telephonyManager.simCountryIso.uppercase(Locale.ROOT)

        return simCountry.ifBlank { "Unknown" }
    }

    private fun getIPAddress(): String {
        return try {
            NetworkInterface.getNetworkInterfaces().asSequence()
                .flatMap { it.inetAddresses.asSequence() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress ?: "Unavailable"
        } catch (e: Exception) {
            "Unavailable"
        }
    }
}