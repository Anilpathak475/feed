package com.twittwe.feedmanager.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager


val Context.networkStatus
    @SuppressLint("MissingPermission")
    get() :Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

