package com.twitter.feed.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = [CustomeStatus::class], version = 1)
abstract class TweetDatabase : RoomDatabase() {

    abstract fun  postDao(): CustomeStatusDao

    companion object {

        private var INSTANCE: TweetDatabase? = null

        private val sLock = Any()

        fun getInstance(context: Context): TweetDatabase {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TweetDatabase::class.java, "Posts.db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE as TweetDatabase
            }
        }
    }
}