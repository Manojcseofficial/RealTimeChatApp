package com.example.realtimechat.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest


/**
 * A utility class to monitor network connectivity changes using Android's [ConnectivityManager].
 *
 * @property context The application or activity context used to access the system's [ConnectivityManager].
 * @property onAvailable A lambda function invoked when a network with internet capabilities becomes available.
 * @property onLost A lambda function invoked when the network is lost or disconnected.
 */
class NetworkState(
    context: Context,
    private val onAvailable: () -> Unit,
    private val onLost: () -> Unit
) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            onAvailable()
        }

        override fun onLost(network: Network) {
            onLost()
        }
    }

    fun register() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
