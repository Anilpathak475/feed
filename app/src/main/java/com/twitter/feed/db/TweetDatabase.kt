package com.twitter.feed.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CustomeStatus::class], version = 1)
abstract class TweetDatabase : RoomDatabase() {

    abstract fun  tweetDao(): CustomeStatusDao

    companion object {

        var INSTANCE: TweetDatabase? = null

        fun getAppDataBase(context: Context): TweetDatabase? {
            if (INSTANCE == null) {
                synchronized(TweetDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, TweetDatabase::class.java, "tweet").build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }

    }
}