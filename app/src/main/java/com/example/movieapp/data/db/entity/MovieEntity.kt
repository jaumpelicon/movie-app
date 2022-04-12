package com.example.movieapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val idMovie: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val rating: Float?,
    val releaseDate: String?,
    val backdropPath: String?
)
