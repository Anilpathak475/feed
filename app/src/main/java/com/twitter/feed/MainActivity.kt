package com.twitter.feed

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.twitter.feed.network.store.TweetStore
import com.twittwe.feedmanager.network.DataCallback
import com.twittwe.feedmanager.network.models.Tweet
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        twitter_login_button.setOnClickListener {
            TweetStore.instance.login(object : DataCallback<AuthResponse> {
                override fun onSuccess(t: AuthResponse?) {
                    Log.d("SUccess", t!!.accessToken)
                    getTimeline(t.accessToken, 1)
                }

                override fun onFailure(error: String) {
                    Log.d("Failed", error)

                }
            })
        }
    }

    private fun getTimeline(accessToken: String, offset: Number) {
        TweetStore.instance.getTimeLine(accessToken, offset, object : DataCallback<List<Tweet>> {
            override fun onSuccess(t: List<Tweet>?) {
                Log.d("SUccess", "" + t!!.size)

            }

            override fun onFailure(error: String) {
                Log.d("Failed", error)

            }

        })
    }
}
