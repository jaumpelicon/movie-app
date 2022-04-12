package com.example.movieapp.ui

data class ViewState<out T>(val status: Status, val data: T?, val message: String? = null) {
    enum class Status {
        SUCCESS, FAILURE, LOADING, INITIAL
    }

    companion object {
        fun <T> success(data: T): ViewState<T> {
            return ViewState(Status.SUCCESS, data)
        }

        fun <T> failure(message: String): ViewState<T> {
            return ViewState(Status.FAILURE, null, message)
        }

        fun <T> initial(): ViewState<T> {
            return ViewState(Status.INITIAL, null)
        }

        fun <T> loading(): ViewState<T> {
            return ViewState(Status.LOADING, null)
        }
    }
}