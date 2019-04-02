package com.twittwe.feedmanager.network.models

import com.google.gson.annotations.SerializedName

class ApiError {
    private val statusCode: Int = 0
    @SerializedName("non_field_errors")
    private val nonFieldError = arrayOfNulls<String>(1)

    @SerializedName("name")
    private val name = arrayOf<String>()

    constructor()

    constructor(nonFieldError: String) {
        try {
            this.nonFieldError[0] = nonFieldError
        } catch (e: Exception) {
            this.nonFieldError[0] = "Something went wrong!"
        }

    }

    fun status(): Int {
        return statusCode
    }

    fun message(): String {
        try {
            if (nonFieldError.isNotEmpty()) {
                val error = nonFieldError[0]
                return if (error != null) {
                    nonFieldError[0]!!
                } else {
                    "Something went wrong!"
                }
            } else {
                if (name.isNotEmpty()) {
                    return name[0]
                }
            }
        } catch (e: Exception) {
            return "Something went wrong!"
        }

        return "Something went wrong!"
    }
}
