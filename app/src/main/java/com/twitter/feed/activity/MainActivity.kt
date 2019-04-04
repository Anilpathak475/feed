package com.twitter.feed.activity

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twitter.feed.*
import com.twitter.feed.BuildConfig.REST_CONSUMER_KEY
import com.twitter.feed.BuildConfig.REST_CONSUMER_SECRET
import com.twitter.feed.adapter.TimelineAdapter
import com.twitter.feed.db.CustomeStatus
import com.twitter.feed.db.CustomeStatusDao
import com.twitter.feed.db.TweetDatabase
import com.twitter.feed.util.Utils
import com.twitter.feed.util.networkStatus
import com.twitter.feed.util.token
import com.twitter.feed.util.tokenSecret
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import twitter4j.*
import twitter4j.auth.AccessToken


class MainActivity : AppCompatActivity() {

    var timelineAdapter: TimelineAdapter? = null
    var twitter: Twitter? = null
    private var db: TweetDatabase? = null
    private var statusDao: CustomeStatusDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerViewTimeline.layoutManager = LinearLayoutManager(this)
        setAdapter()
    }

    private fun setAdapter() {
        timelineAdapter = TimelineAdapter()
        recyclerViewTimeline.adapter = timelineAdapter
        setBottomReachedListener()
        createTwitterSession()
    }

    private fun setBottomReachedListener() {
        recyclerViewTimeline.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (recyclerView.adapter!!.itemCount > 0 && !recyclerView.canScrollVertically(5)) {
                    Log.d("Item count", "" + timelineAdapter!!.itemCount)
                    GetTimeline(timelineAdapter!!.itemCount + 1).execute()

                }
            }
        })
    }

    private fun createTwitterSession() {
        val accessToken = AccessToken(this.token, this.tokenSecret)
        val factory = TwitterFactory()
        twitter = factory.instance
        twitter!!.setOAuthConsumer(
            REST_CONSUMER_KEY,
            REST_CONSUMER_SECRET
        )
        twitter!!.oAuthAccessToken = accessToken
        if (networkStatus)
            GetTimeline(1).execute()
        else {
            Toast.makeText(
                this,
                "No Internet detected. Rest Assure you can still read last loaded feeds.",
                Toast.LENGTH_SHORT
            ).show()
            Observable.fromCallable {
                db = TweetDatabase.getAppDataBase(this)
                statusDao = db?.postDao()

                with(statusDao) {
                    this?.fetchAllTasks(this.getCount().toString())
                }
            }.doOnNext { list ->
                if (list.size > 0)
                    pushTweetsToAdapter(list)
                else
                    noInternetDialog()
            }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
        }
    }

    private fun pushTweetsToAdapter(tweets: MutableList<CustomeStatus>) {
        timelineAdapter!!.addTweets(tweets)

    }

    private fun noInternetDialog() {

        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No Internet Connection")
            builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit")

            builder.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }

            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class GetTimeline(private val offset: Int) : AsyncTask<Void, Void, ResponseList<Status>>() {
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
                tweets != null -> {
                    val convertedTweets = Utils().convertStatusToCustomeStatus(tweets)
                    pushTweetsToAdapter(convertedTweets)
                    storeTweetsInDatabase(convertedTweets)
                }

            }
        }
    }

    fun storeTweetsInDatabase(tweets: MutableList<CustomeStatus>?) {
        Observable.fromCallable {
            statusDao = db?.postDao()
            if (statusDao!!.getCount() > 60) {
                statusDao!!.deleteAll()
            }
            with(statusDao) {
                this?.saveAll(tweets!!)
            }
            statusDao!!.getCount()
        }.doOnNext {

            Log.d("Records is database", "" + it)

        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
    }

}
