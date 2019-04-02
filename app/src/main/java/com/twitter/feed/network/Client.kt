package com.twitter.feed.network

import com.twitter.feed.AuthResponse
import com.twittwe.feedmanager.network.models.Tweet
import retrofit2.Call
import retrofit2.http.*

interface TweetApiClient {


    @GET("statuses/home_timeline.json")
    fun getTimeLines(
        @Query("Name") name: String,
        @Header("Authorization") session: String
    ): Call<List<Tweet>>

    @POST("/oauth2/token")
    fun login(
        @Header("Authorization") session: String,
        @Header("Content-Type") contentType: String,
        @Query("grant_type") granType: String
    ): Call<AuthResponse>
}

data class LoginBody(val grant_type: String = "client_credentials")

