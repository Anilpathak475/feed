package com.twittwe.feedmanager.network

interface DataCallback<T> {
    fun onSuccess(t: T?)

    fun onFailure(error: String)
}
