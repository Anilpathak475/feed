package com.twitter.feed.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.twitter.feed.db.CustomeStatus
import twitter4j.*


val Context.networkStatus
    @SuppressLint("MissingPermission")
    get() :Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

val Context.token
    @SuppressLint("MissingPermission")
    get() : String? {
       return SharedPreferenceManager.getInstance(this).getValue(SessionToken)
    }
val Context.tokenSecret
    @SuppressLint("MissingPermission")
    get() : String? {
       return SharedPreferenceManager.getInstance(this).getValue(SessionTokenSecret)
    }

class Utils {

      fun  convertStatusToCustomeStatus(statuss: ResponseList<Status>): MutableList<CustomeStatus> {
        val customeStatuss: MutableList<CustomeStatus> = ArrayList()
        statuss.forEach { status ->
            run {
                val customeStatus = CustomeStatus()
                customeStatus.biggerProfileImageURL = status.user.biggerProfileImageURL
                customeStatus.text = status.text
                customeStatus.description = status.user.description
                customeStatus.name = status.user.name
                customeStatuss.add(customeStatus)
            }
        }
        return customeStatuss
    }


}



