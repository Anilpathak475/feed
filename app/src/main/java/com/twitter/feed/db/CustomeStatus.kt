package com.twitter.feed.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class CustomeStatus {

    @PrimaryKey(autoGenerate = true)
    private  var id:Int = 0
    var biggerProfileImageURL: String = ""
    var name: String = ""
    var description:String = ""
    var text:String = ""
    val tweetId:String = ""
}