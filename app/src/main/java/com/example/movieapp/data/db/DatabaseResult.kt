package com.example.movieapp.data.db

data class DatabaseResult<out T>(val status: Status, val data: T?, val message: String? = null) {
    enum class Status {
        SUCCESS, FAILURE
    }

    companion object {
        fun <T> success(data: T): DatabaseResult<T> {
            return DatabaseResult(Status.SUCCESS, data)
        }

        fun <T> failure(message: String): DatabaseResult<T> {
            return DatabaseResult(Status.FAILURE, null, message)
        }
    }
}
