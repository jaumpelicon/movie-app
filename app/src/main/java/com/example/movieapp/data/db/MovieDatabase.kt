package com.example.movieapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movieapp.data.db.dao.MovieDAO
import com.example.movieapp.data.db.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract val movieDAO: MovieDAO

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            synchronized(this) {
                var instance: MovieDatabase? = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        MovieDatabase::class.java,
                        "movie_database"
                    ).build()
                }
                return instance
            }
        }
    }
}