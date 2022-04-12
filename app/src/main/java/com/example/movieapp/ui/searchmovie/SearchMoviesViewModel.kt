package com.example.movieapp.ui.searchmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.Movie
import com.example.movieapp.data.network.NetworkResult
import com.example.movieapp.data.repository.MovieRepository
import com.example.movieapp.ui.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchMoviesViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _moviesState =
        MutableStateFlow(ViewState(ViewState.Status.INITIAL, emptyList<Movie>()))
    private var movies: MutableList<Movie> = ArrayList()
    val moviesState = _moviesState.asStateFlow()
    private var page = 1
    var adultFilter = true
    lateinit var movieName: String

    fun getFirstSearchMovies() {
        page = 1
        movies.clear()
        getSearchMovies(page, movieName, adultFilter)
    }

    fun getMoreSearchMovies() {
        page++
        getSearchMovies(page, movieName, adultFilter)
    }

    private fun getSearchMovies(page: Int, movieName: String, filter: Boolean) {
        _moviesState.value = ViewState.loading()
        viewModelScope.launch {
            repository.getSearchMovies(page, movieName, filter).collect { value ->
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