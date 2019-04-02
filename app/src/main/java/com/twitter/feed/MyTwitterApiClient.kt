package com.twitter.feed

import android.app.PendingIntent.getService
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

 interface FriendshipsService {
     @GET("/1.1/statuses/home_timeline.json")
     fun getTimeLines(
         @Query("since_id") lastId: Number,
         @Query("count") count: Number,
         @Query("screen_name") name: String
     ): Call<ResponseBody>
 }

