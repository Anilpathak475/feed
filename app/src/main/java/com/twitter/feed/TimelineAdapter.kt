package com.twitter.feed

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.twitter.feed.db.CustomeStatus
import kotlinx.android.synthetic.main.item_tweet.view.*
import twitter4j.ResponseList
import twitter4j.Status
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimelineAdapter(var tweets: ArrayList<CustomeStatus>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {
    fun addTweets(tweets: ArrayList<CustomeStatus>) {
        this.tweets.addAll(tweets)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TimelineViewHolder {
        val layoutInflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = layoutInflater.inflate(R.layout.item_tweet, viewGroup, false)
        return TimelineViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val tweet = tweets[holder.adapterPosition]
        Picasso.get().load(tweet.biggerProfileImageURL).fit().centerCrop().into(holder.itemView.imageViewAvatar)
        holder.itemView.textViewUserName.text = tweet.name
        holder.itemView.textViewuserHandel.text = tweet.description
        holder.itemView.textViewTweetText.text = tweet.text
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    inner class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface StatusClick {
        fun onItemClick(status: Status)
    }

}