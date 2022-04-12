package com.example.movieapp.ui.favoritemovies

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import com.example.movieapp.ui.ViewState
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.adapter.FavoriteMovieAdapter
import com.example.movieapp.data.db.MovieDatabase
import com.example.movieapp.data.model.Movie
import com.example.movieapp.data.repository.DatabaseRepository
import com.example.movieapp.databinding.FragmentFavoriteMoviesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteMoviesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteMoviesBinding
    private val adapterFavorite = FavoriteMovieAdapter()
    private val viewModel: FavoriteMoviesViewModel by viewModel {
        val movieDAO = MovieDatabase.getInstance(requireContext()).movieDAO
        parametersOf(DatabaseRepository(movieDAO))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_movies, container, false)
        initRecyclerView()
        initObserver()
        clickItemRecycler()
        clickItemRemoveRecycler()
        binding.floatingActionButtonDeleteAllFavorites.setOnClickListener {
            deleteAllFavoriteMovies()
        }
        return binding.root
    }

    private fun deleteAllFavoriteMovies() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setNegativeButton(getString(R.string.dialog_negative)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
                viewModel.deleteAllMovies()
            }
            .show()
    }

    private fun clickItemRecycler() {
        adapterFavorite.itemClick = {
            val movie = Movie(
                it.id,
                it.title,
                it.overview,
                it.posterPath,
                it.rating,
                it.releaseDate,
                it.backdropPath
            )
            findNavController().navigate(
                FavoriteMoviesFragmentDirections.actionFavoriteMoviesFragmentToDetailsMovieFragment(
                    movie, false
                )
            )
        }
    }

    private fun clickItemRemoveRecycler() {
        adapterFavorite.itemClickRemove = {
            viewModel.deleteMovie(it)
        }
    }

    private fun initRecyclerView() {
        binding.recyclerViewFavorites.adapter = adapterFavorite
        binding.recyclerViewFavorites.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState.collect {
                    when (it.status) {
                        ViewState.Status.INITIAL -> {
                            binding.floatingActionButtonDeleteAllFavorites.visibility = GONE
                            binding.recyclerViewFavorites.visibility = GONE
                            binding.feedbackFavorites.visibility = GONE
                            binding.progressBarFavorite.visibility = GONE
                            binding.textProgressBarFavorite.visibility = GONE
                        }

                        ViewState.Status.LOADING -> {
                            binding.floatingActionButtonDeleteAllFavorites.visibility = GONE
                            binding.recyclerViewFavorites.visibility = GONE
                            binding.feedbackFavorites.visibility = GONE
                            binding.progressBarFavorite.visibility = VISIBLE
                            binding.textProgressBarFavorite.visibility = VISIBLE
                        }

                        ViewState.Status.FAILURE -> {
                            binding.floatingActionButtonDeleteAllFavorites.visibility = GONE
                            binding.recyclerViewFavorites.visibility = GONE
                            binding.progressBarFavorite.visibility = GONE
                            binding.textProgressBarFavorite.visibility = GONE
                            binding.feedbackFavorites.visibility = VISIBLE
                            binding.feedbackFavorites.text = it.message
                        }

                        ViewState.Status.SUCCESS -> {
                            binding.feedbackFavorites.visibility = GONE
                            binding.progressBarFavorite.visibility = GONE
                            binding.textProgressBarFavorite.visibility = GONE
                            binding.floatingActionButtonDeleteAllFavorites.visibility = VISIBLE
                            binding.recyclerViewFavorites.visibility = VISIBLE
                            adapterFavorite.updateDataMovie(it.data!!)
                            it.data.let { movies ->
                                if (movies.isNotEmpty()) {
                                    adapterFavorite.updateDataMovie(movies)
                                } else {
                                    binding.feedbackFavorites.visibility = VISIBLE
                                    binding.feedbackFavorites.text =
                                        getString(R.string.feedback_null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}