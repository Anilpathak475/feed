package com.twitter.feed

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_tweet.view.*
import twitter4j.ResponseList
import twitter4j.Status
import java.text.SimpleDateFormat
import java.util.*


class TimelineAdapter(var tweets: ResponseList<Status>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {
    fun addTweets(tweets: ResponseList<twitter4j.Status>) {
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
        Picasso.get().load(tweet.user.biggerProfileImageURL).fit().centerCrop().into(holder.itemView.imageViewAvatar)
        holder.itemView.textViewUserName.text = tweet.user.name
        holder.itemView.textViewuserHandel.text = tweet.user.description
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