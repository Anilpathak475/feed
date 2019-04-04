package com.twitter.feed

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import twitter4j.*
import twitter4j.auth.AccessToken


class MainActivity : AppCompatActivity() {

    var timelineAdapter: TimelineAdapter? = null
    var twitter: Twitter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val REST_CONSUMER_KEY = "hK9CjoMkZ8DtDnfYUrLKIMDNT"
        val REST_CONSUMER_SECRET = "1EQ8wbAblYddd8edp1D9mKwDS6f5EaRCnlqyI7ewGqabpPQRap" // Change this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerViewTimeline.layoutManager = LinearLayoutManager(this);

        getExtras()

    }

    private fun setBottomReachedListener() {
        recyclerViewTimeline.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(5)) {
                    InitiateSession(recyclerView.childCount + 1).execute()

                }
            }
        })
    }

    private fun getExtras() {
        val bundle = intent.extras
        if (bundle.containsKey("session")) {
            val session = bundle.getString("session")
            val sessionSecret = bundle.getString("sessionSecret")
            val accessToken = AccessToken(session, sessionSecret)
            val factory = TwitterFactory()
            twitter = factory.instance
            twitter!!.setOAuthConsumer(
                "hK9CjoMkZ8DtDnfYUrLKIMDNT",
                "1EQ8wbAblYddd8edp1D9mKwDS6f5EaRCnlqyI7ewGqabpPQRap"
            )
            twitter!!.oAuthAccessToken = accessToken

            InitiateSession(1).execute()
        }
    }

    private fun setAdapter(tweets: ResponseList<twitter4j.Status>?) {
        timelineAdapter = TimelineAdapter(tweets!!)
        recyclerViewTimeline.adapter = timelineAdapter

    }


    inner class InitiateSession(private val offset: Int) : AsyncTask<Void, Void, ResponseList<Status>>() {
        override fun doInBackground(vararg p0: Void?): ResponseList<twitter4j.Status>? {
            val totalTweets = 20 // no of tweets to be fetched
            val paging = Paging(offset, totalTweets)
            return try {
                twitter!!.getHomeTimeline(paging)
            } catch (e: TwitterException) {
                null
            }
        }

        override fun onPostExecute(tweets: ResponseList<twitter4j.Status>?) {
            super.onPostExecute(tweets)
            when {
                tweets != null -> if (timelineAdapter != null)
                    timelineAdapter!!.addTweets(tweets)
                else {
                    setAdapter(tweets)
                    setBottomReachedListener()
                }
            }
        }
    }

}
