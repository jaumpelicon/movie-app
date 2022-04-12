package com.example.movieapp.data.network

import com.example.movieapp.data.model.MovieResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("movie/top_rated?api_key=${apiKey}&language=${language}")
    suspend fun getRatedMovies(
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/upcoming?api_key=${apiKey}&language=${language}")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("movie/popular?api_key=${apiKey}&language=${language}")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("search/movie?api_key=${apiKey}&language=${language}")
    suspend fun getSearchMovies(
        @Query("query") movieName: String,
        @Query("page") page: Int,
        @Query("include_adult") adultFilter: Boolean
    ): Response<MovieResponse>

    companion object {

        private const val apiKey = "50ee3323cd11495d7ba94af454c1d1b0"
        private const val language = "pt-BR"
        private val retrofitService: MovieService by lazy {

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(MovieService::class.java)
        }

        fun getInstance(): MovieService {
            return retrofitService
        }
    }
}