package com.example.movieapp.ui.favoritemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.db.DatabaseResult
import com.example.movieapp.data.db.entity.MovieEntity
import com.example.movieapp.data.repository.DatabaseRepository
import com.example.movieapp.ui.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteMoviesViewModel(private val database: DatabaseRepository) : ViewModel() {

    private var movies: MutableList<MovieEntity> = ArrayList()
    private val _moviesState =
        MutableStateFlow(ViewState(ViewState.Status.INITIAL, emptyList<MovieEntity>()))
    val moviesState = _moviesState.asStateFlow()

    init {
        getAllMovies()
    }

   private fun getAllMovies() {
        _moviesState.value = ViewState.loading()
        viewModelScope.launch {
            database.getAll().collect { value ->
                when (value.status) {
                    DatabaseResult.Status.SUCCESS -> {
                        value.data?.let {
                            val newMovies = value.data
                            movies = newMovies as MutableList<MovieEntity>
                            _moviesState.value = ViewState.success(movies)
                        }
                    }

                    DatabaseResult.Status.FAILURE -> {
                        val message = value.message ?: "Ocorreu um erro!"
                        _moviesState.value = ViewState.failure(message)
                    }
                }
            }
        }
    }

    fun deleteAllMovies() {
        viewModelScope.launch {
            database.deleteAllFavoriteMovies()
            movies.clear()
            getAllMovies()
        }
    }

    fun deleteMovie(movieEntity: MovieEntity) {
        viewModelScope.launch {
            database.removeMovie(movieEntity.idMovie)
            movies.remove(movieEntity)
            getAllMovies()
        }
    }
}
