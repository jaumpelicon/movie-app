package com.example.movieapp.ui.topratedmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.Movie
import com.example.movieapp.data.network.NetworkResult
import com.example.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.movieapp.ui.ViewState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RatedMoviesViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _moviesState =
        MutableStateFlow(ViewState(ViewState.Status.INITIAL, emptyList<Movie>()))
    private var movies: MutableList<Movie> = ArrayList()
    private var page = 0
    val moviesState = _moviesState.asStateFlow()

    init {
        getFirstRatedMovies()
    }

    private fun getFirstRatedMovies() {
        page = 1
        movies.clear()
        getRatedMovies(page)
    }

    fun getMoreRatedMovies() {
        page++
        getRatedMovies(page)
    }

    private fun getRatedMovies(page: Int) {
        _moviesState.value = ViewState.loading()
        viewModelScope.launch {
            repository.getRatedMovies(page).collect { value ->
                when (value) {
                    is NetworkResult.Success -> {
                        value.data?.let {
                            val newMovies = value.data.movies
                            movies.addAll(newMovies)
                            _moviesState.value = ViewState.success(movies)
                        }
                    }

                    is NetworkResult.Error -> {
                        _moviesState.value = value.message?.let { ViewState.failure(it) }!!
                    }

                    is NetworkResult.Loading -> {
                        _moviesState.value = ViewState.loading()
                    }
                }
            }
        }
    }
}