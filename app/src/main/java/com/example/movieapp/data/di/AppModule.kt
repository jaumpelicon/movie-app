package com.example.movieapp.data.di

import com.example.movieapp.data.network.MovieService
import com.example.movieapp.data.repository.DatabaseRepository
import com.example.movieapp.data.repository.MovieRepository
import com.example.movieapp.ui.details.DetailsMoviesViewModel
import com.example.movieapp.ui.favoritemovies.FavoriteMoviesViewModel
import com.example.movieapp.ui.popularmovies.PopularMoviesViewModel
import com.example.movieapp.ui.searchmovie.SearchMoviesViewModel
import com.example.movieapp.ui.topratedmovies.RatedMoviesViewModel
import com.example.movieapp.ui.upcomingmovies.UpcomingMoviesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { parameters ->
        PopularMoviesViewModel(MovieRepository(MovieService.getInstance(), parameters.get()))
    }

    viewModel { parameters ->
        RatedMoviesViewModel(MovieRepository(MovieService.getInstance(), parameters.get()))
    }

    viewModel { parameters ->
        UpcomingMoviesViewModel(MovieRepository(MovieService.getInstance(), parameters.get()))
    }

    viewModel { parameters ->
        SearchMoviesViewModel(MovieRepository(MovieService.getInstance(), parameters.get()))
    }
}

val databaseModule = module {

    viewModel { (database: DatabaseRepository) ->
        DetailsMoviesViewModel(database)
    }

    viewModel { (database: DatabaseRepository) ->
        FavoriteMoviesViewModel(database)
    }
}
