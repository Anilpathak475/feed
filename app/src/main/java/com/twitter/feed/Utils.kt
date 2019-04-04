package com.twitter.feed

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.squareup.picasso.Picasso
import com.twitter.feed.db.CustomeStatus
import kotlinx.android.synthetic.main.item_tweet.view.*
import twitter4j.*


val Context.networkStatus
    @SuppressLint("MissingPermission")
    get() :Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

class Utils {
    public  fun  convertStatusToCustomeStatus(statuss: ResponseList<Status>): ArrayList<CustomeStatus> {

        val customeStatuss: ArrayList<CustomeStatus> = ArrayList()
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



