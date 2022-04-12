package com.example.movieapp.data.repository

import com.example.movieapp.data.model.MovieResponse
import com.example.movieapp.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val retrofitService: MovieService, networkChecker: Boolean) : BaseApiResponse(networkChecker) {

    suspend fun getUpcomingMovies(page: Int): Flow<NetworkResult<MovieResponse>> {
        return flow {
            emit(safeApiCall { retrofitService.getUpcomingMovies(page) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getRatedMovies(page: Int): Flow<NetworkResult<MovieResponse>> {
        return flow {
            emit(safeApiCall { retrofitService.getRatedMovies(page) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPopularMovies(page: Int): Flow<NetworkResult<MovieResponse>> {
        return flow {
            emit(safeApiCall { retrofitService.getPopularMovies(page) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getSearchMovies(
        page: Int,
        movieName: String,
        adultFilter: Boolean
    ): Flow<NetworkResult<MovieResponse>> {
        return flow {
            emit(safeApiCall { retrofitService.getSearchMovies(movieName, page, adultFilter) })
        }.flowOn(Dispatchers.IO)
    }
}
