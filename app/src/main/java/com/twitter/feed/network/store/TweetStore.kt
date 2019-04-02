package com.twitter.feed.network.store

import android.util.Base64
import androidx.annotation.NonNull
import com.twitter.feed.AuthResponse
import com.twitter.feed.network.LoginBody
import com.twitter.feed.network.TweetApiClient
import com.twittwe.feedmanager.network.ClientGenerator
import com.twittwe.feedmanager.network.DataCallback
import com.twittwe.feedmanager.network.models.Tweet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException

class TweetStore private constructor() {

    private val clientGenerator: ClientGenerator = ClientGenerator()

    private val REST_CONSUMER_KEY = "hK9CjoMkZ8DtDnfYUrLKIMDNT"
    private val REST_CONSUMER_SECRET = "1EQ8wbAblYddd8edp1D9mKwDS6f5EaRCnlqyI7ewGqabpPQRap" // Change this

    companion object {
        val instance: TweetStore
            get() = TweetStore()
    }

    fun getTimeLine(session: String, lastIndex: Number, dataCallback: DataCallback<List<Tweet>>) {
        val tweetClient = clientGenerator.createClient(TweetApiClient::class.java)
        val call = tweetClient.getTimeLines(/*lastIndex,*/ /*30, */"AnilPathak", "Bearer $session")
        call.enqueue(object : Callback<List<Tweet>> {
            override fun onResponse(@NonNull call: Call<List<Tweet>>, @NonNull response: Response<List<Tweet>>) {
                if (response.isSuccessful && response.body() != null) {
                    dataCallback.onSuccess(response.body())
                } else {
                    dataCallback.onFailure("")
                }
            }

            override fun onFailure(@NonNull call: Call<List<Tweet>>, @NonNull t: Throwable) {
                dataCallback.onFailure(t.localizedMessage)
            }
        })
    }

    fun login(dataCallback: DataCallback<AuthResponse>) {
        val tweetClient = clientGenerator.createClient(TweetApiClient::class.java)
        val authKey = encodeString("$REST_CONSUMER_KEY:$REST_CONSUMER_SECRET")
        val call = tweetClient.login(authKey, "application/x-www-form-urlencoded;charset=UTF-8", LoginBody().grant_type)
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(@NonNull call: Call<AuthResponse>, @NonNull response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    dataCallback.onSuccess(response.body())
                } else {
                    dataCallback.onFailure("")
                }
            }

            override fun onFailure(@NonNull call: Call<AuthResponse>, @NonNull t: Throwable) {
                dataCallback.onFailure(t.localizedMessage)
            }
        })
    }

    private fun encodeString(s: String): String {
        var data = ByteArray(0)

        try {
            data = s.toByteArray(charset("UTF-8"))

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } finally {

            return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)

        }
    }


}
