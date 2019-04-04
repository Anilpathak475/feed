package com.twitter.feed.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class CustomeStatus {
    @PrimaryKey(autoGenerate = true)
    private  var id:Int = 0
    val cbiggerProfileImageURL: String = ""
    val name: String = ""
    val description:String = ""
    val text:String = ""
    val tweetId:String = ""
}