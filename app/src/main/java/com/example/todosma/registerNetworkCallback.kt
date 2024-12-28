package com.example.todosma

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

fun registerNetworkCallback(context: Context) {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("NetworkCallback", "Internet is back. Syncing Room with Firebase.")
            syncRoomToFirebase(context) // Trigger sync
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("NetworkCallback", "Internet connection lost.")
        }
    })
}