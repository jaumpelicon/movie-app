package com.example.movieapp.data.network

import retrofit2.Response

abstract class BaseApiResponse(private val networkChecker: Boolean) {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        if (networkChecker) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        return NetworkResult.Success(body)
                    }
                }
                return error("${response.code()} ${response.message()}")
            } catch (e: Exception) {
                return error(e.message ?: e.toString())
            }
        }
        return error("sem conexão com a internet!")
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Falha na chamada de rede: $errorMessage")
}