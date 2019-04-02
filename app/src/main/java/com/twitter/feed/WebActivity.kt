package com.twitter.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("session", accessToken.token)
        intent.putExtra("sessionSecret", accessToken.tokenSecret)
        startActivity(intent)
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
    private val REST_CONSUMER_KEY = "hK9CjoMkZ8DtDnfYUrLKIMDNT"
    private val REST_CONSUMER_SECRET = "1EQ8wbAblYddd8edp1D9mKwDS6f5EaRCnlqyI7ewGqabpPQRap" // Change this
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
        view!!.start(REST_CONSUMER_KEY, REST_CONSUMER_SECRET, TWITTER_CALLBACK_URL, DUMMY_CALLBACK_URL, this)
    }


}
