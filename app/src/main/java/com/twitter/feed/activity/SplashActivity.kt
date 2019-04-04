package com.twitter.feed.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.twitter.feed.R
import com.twitter.feed.util.token
import com.twitter.feed.util.tokenSecret

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent: Intent = if (TextUtils.isEmpty(token) && TextUtils.isEmpty(tokenSecret)) {
                Intent(this@SplashActivity, WebActivity::class.java)
            } else {
                Intent(this@SplashActivity, MainActivity::class.java)

            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
