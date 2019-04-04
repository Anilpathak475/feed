package com.twitter.feed.util

import android.content.Context
import android.content.SharedPreferences
import com.twitter.feed.BuildConfig

/**
 * Created by anilpathak on 05/09/17.
 */

class SharedPreferenceManager private constructor(
    var context: Context
) {


    private val sharedPreferences: SharedPreferences
        get() {
            val prefName = BuildConfig.APPLICATION_ID + ".Prefs"
            return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        }

    fun saveValue(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun clearValue(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clearUserData() {
        sharedPreferences.all.clear()
    }

    fun getValue(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    companion object {
        fun getInstance(context: Context): SharedPreferenceManager {
            return SharedPreferenceManager(context)
        }
    }
}
