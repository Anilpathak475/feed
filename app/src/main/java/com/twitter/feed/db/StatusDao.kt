package com.twitter.feed.db

import android.arch.persistence.room.*
import androidx.lifecycle.LiveData

@Dao
public interface CustomeStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(posts: List<CustomeStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(post: CustomeStatus)

    @Update
    fun update(post: CustomeStatus)

    @Delete
    fun delete(post: CustomeStatus)

    @Query("SELECT * FROM CustomeStatus")
    fun findAll(): LiveData<List<CustomeStatus>>
}