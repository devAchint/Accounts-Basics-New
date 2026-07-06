package com.techuntried.accountsbasics2.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class RealNetworkMonitor(context: Context) : NetworkMonitor {
    
    // Get the system service from the context
    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean {
        // 1. Get the currently active network
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        
        // 2. Get the capabilities (Wi-Fi, Cellular, Ethernet, etc.)
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        
        // 3. Specifically check if it has "validated" internet access
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isConnected())
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                // This triggers when the network officially becomes "VALIDATED"
                trySend(isConnected())
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        // We only care about networks that claim to have internet
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send the initial state immediately
        trySend(isConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged() // Prevents spamming the ViewModel if the state hasn't changed


}