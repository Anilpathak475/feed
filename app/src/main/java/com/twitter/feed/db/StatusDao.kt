package com.twitter.feed.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
public interface CustomeStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(posts: MutableList<CustomeStatus>)

    @Query("Select * from  CustomeStatus")
    fun fetchAllTasks(): MutableList<CustomeStatus>

    @Query("Select COUNT(*) from  CustomeStatus")
    fun getCount(): Int

    @Query("SELECT * FROM CustomeStatus order by id==:id desc")
    fun fetchAllTasks(id: String): MutableList<CustomeStatus>

    @Query("Delete FROM CustomeStatus")
    fun deleteAll()
}