package com.twitter.feed.db

import android.arch.persistence.room.*
import android.provider.ContactsContract
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

    @Query("Select * from  CustomeStatus")
    fun fetchAllTasks(): LiveData<List<CustomeStatus>>

    @Query("SELECT * FROM CustomeStatus order by id==:id desc")
    fun fetchAllTasks(id: String): List<CustomeStatus>

    @Update
    fun updateTask(note: ContactsContract.CommonDataKinds.Note)
}