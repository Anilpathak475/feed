package com.twitter.feed.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.twitter.feed.*
import com.twitter.feed.customview.TwitterOAuthView
import com.twitter.feed.util.SessionToken
import com.twitter.feed.util.SessionTokenSecret
import com.twitter.feed.util.SharedPreferenceManager
import twitter4j.Twitter
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken


class WebActivity : AppCompatActivity(), TwitterOAuthView.Listener {
    override fun onSuccess(view: TwitterOAuthView, accessToken: AccessToken) {
        // The application has been authorized and an access token
        // has been obtained successfully. Save the access token
        // for later use.
        Log.d("Token", accessToken.token)
        showMessage("Authorized by " + accessToken.screenName)
        sharedPreferenceManager.saveValue(SessionToken, accessToken.token)
        sharedPreferenceManager.saveValue(SessionTokenSecret, accessToken.tokenSecret)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onFailure(view: TwitterOAuthView, result: TwitterOAuthView.Result) {
        // Failed to get an access token.
        showMessage("Failed due to $result")
    }

    private fun showMessage(message: String) {
        // Show a popup message.
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private var oauthStarted: Boolean = false
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    private var accessToken = ""
    private var TWITTER_CALLBACK_URL = "twittersdk://"
    private val DUMMY_CALLBACK_URL = true

    private val twitter: Twitter? = null
    private var requestToken: RequestToken? = null
    private var view: TwitterOAuthView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        view = TwitterOAuthView(this)
        sharedPreferenceManager = SharedPreferenceManager.getInstance(this)
        view!!.isDebugEnabled = true

        setContentView(view)

        oauthStarted = false
    }

    override fun onResume() {
        super.onResume()

        if (oauthStarted) {
            return
        }

        oauthStarted = true

        // Start Twitter OAuth process. Its result will be notified via
        // TwitterOAuthView.Listener interface.
        view!!.start(
            BuildConfig.REST_CONSUMER_KEY, BuildConfig.REST_CONSUMER_SECRET
            , TWITTER_CALLBACK_URL, DUMMY_CALLBACK_URL, this
        )
    }


}
