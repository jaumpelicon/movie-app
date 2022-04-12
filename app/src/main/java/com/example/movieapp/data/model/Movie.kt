package com.example.movieapp.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val rating: Float?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("backdrop_path") val backdropPath: String?
): Serializable
