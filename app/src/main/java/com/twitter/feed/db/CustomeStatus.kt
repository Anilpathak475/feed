package com.twitter.feed.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CustomeStatus(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var biggerProfileImageURL: String = "",
    var name: String = "",
    var description: String = "",
    var text: String = "",
    val tweetId: String = ""
)