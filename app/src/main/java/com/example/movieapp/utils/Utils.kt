package com.example.movieapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Utils {

    fun hasInternetConnection(context: Context?): Boolean {
        if (context == null)
            return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return when (activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                else -> false
            }
        }
    }

}
