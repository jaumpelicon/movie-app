package com.example.movieapp.ui.details

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

class DetailsMoviesViewModel(private val database: DatabaseRepository) : ViewModel() {

    private val _moviesState =
        MutableStateFlow(ViewState(ViewState.Status.INITIAL, 0))
    val moviesState = _moviesState.asStateFlow()

    fun findMovieDB(movieEntity: MovieEntity, boolean: Boolean) {
        _moviesState.value = ViewState.loading()
        viewModelScope.launch {
            database.findMovie(movieEntity.idMovie).collect { value ->
                when (value.status) {
                    DatabaseResult.Status.SUCCESS -> {
                        value.data.let {
                            if (value.data == false && !boolean) {
                                //Não existe e não clicou no botão
                                _moviesState.value = ViewState.success(1)
                            } else if (value.data == false && boolean) {
                                //Não existe e clicou no botão
                                _moviesState.value = ViewState.success(2)
                            } else if (value.data != false && !boolean) {
                                //Existe e não clicou no botão
                                _moviesState.value = ViewState.success(3)
                            } else {
                                //Existe e clicou no botão
                                _moviesState.value = ViewState.success(4)
                            }
                        }
                    }

                    DatabaseResult.Status.FAILURE -> {
                        _moviesState.value = value.message?.let { ViewState.failure(it) }!!
                    }
                }
            }
        }
    }

    fun addMovie(movieEntity: MovieEntity) {
        _moviesState.value = ViewState.loading()
        viewModelScope.launch {
            database.addMovie(movieEntity)
        }
    }

    fun dellMovie(movieEntity: MovieEntity){
        _moviesState.value=ViewState.loading()
        viewModelScope.launch {
            database.removeMovie(movieEntity.idMovie)
        }
    }
}