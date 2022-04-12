package com.example.movieapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.data.db.entity.MovieEntity

@Dao
interface MovieDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movieEntity: MovieEntity)

    @Query("SELECT * FROM tb_movie")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("DELETE FROM tb_movie WHERE idMovie = :idMovie")
    suspend fun deleteMovie(idMovie: Int)

    @Query("DELETE FROM tb_movie")
    suspend fun deleteAllMovies()

    @Query("SELECT EXISTS(SELECT * FROM tb_movie WHERE idMovie = :id)")
    suspend fun findMovie(id: Int): Boolean
}
