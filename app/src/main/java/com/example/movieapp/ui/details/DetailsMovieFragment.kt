package com.example.movieapp.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.movieapp.R
import com.example.movieapp.R.drawable
import com.example.movieapp.data.db.MovieDatabase
import com.example.movieapp.data.db.entity.MovieEntity
import com.example.movieapp.data.repository.DatabaseRepository
import com.example.movieapp.databinding.FragmentDetailsMovieBinding
import com.example.movieapp.ui.ViewState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class DetailsMovieFragment : Fragment() {

    private lateinit var binding: FragmentDetailsMovieBinding
    private lateinit var args: DetailsMovieFragmentArgs
    private val viewModel: DetailsMoviesViewModel by viewModel {
        val movieDAO = MovieDatabase.getInstance(requireContext()).movieDAO
        parametersOf(DatabaseRepository(movieDAO))
    }
    private lateinit var movieEntity: MovieEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_details_movie, container, false)
        initObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt-BR", "Brazil"))
        args = DetailsMovieFragmentArgs.fromBundle(requireArguments())
        if (args.detailsVerifier) {
            binding.buttonAddFavorite.visibility = View.VISIBLE
        }
        binding.movieTitleDetails.text = args.movie.title

        args.movie.backdropPath.let {
            when {
                it != null -> {
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500/${args.movie.backdropPath}")
                        .into(binding.movieBackdropDetails)
                }
                else -> {
                    binding.movieBackdropDetails.setImageResource(drawable.ic_baseline_image_not_supported_24)
                }
            }
        }

        args.movie.posterPath.let {
            when {
                it != null -> {
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500/${args.movie.posterPath}")
                        .into(binding.moviePosterDetails)
                }
                else -> {
                    binding.moviePosterDetails.setImageResource(drawable.ic_baseline_image_not_supported_24)
                }
            }
        }

        args.movie.overview.let {
            val overview = "Em breve"
            if (it.isNullOrEmpty()) {
                binding.movieSynopsisTextDetails.text = overview
            } else {
                binding.movieSynopsisTextDetails.text = args.movie.overview
            }
        }

        args.movie.rating.let {
            val rating = 0F
            if (it == 0F || it == null) {
                binding.ratingBar.rating = rating
                binding.ratingBar.stepSize = .5f
            } else {
                binding.ratingBar.rating = args.movie.rating!! / 2
                binding.ratingBar.stepSize = .5f
            }
        }

        args.movie.releaseDate.let {
            val date = "--/--/----"
            if (it.isNullOrEmpty()) {
                binding.movieDateTextDetails.text = date
            } else {
                binding.movieDateTextDetails.text =
                    formatter.format(parser.parse(args.movie.releaseDate!!)!!)
            }
        }

        movieEntity = MovieEntity(
            0,
            args.movie.id,
            args.movie.title,
            args.movie.overview,
            args.movie.posterPath,
            args.movie.rating,
            args.movie.releaseDate,
            args.movie.backdropPath
        )

        lifecycleScope.launch {
            viewModel.findMovieDB(movieEntity, false)
        }

        binding.buttonAddFavorite.setOnClickListener {
            lifecycleScope.launch {
                viewModel.findMovieDB(movieEntity, true)
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState.collect {
                    when (it.status) {
                        ViewState.Status.SUCCESS -> {
                            when (it.data) {
                                1 -> {
                                    binding.buttonAddFavorite.setImageResource(drawable.ic_baseline_playlist_add_24)
                                }
                                2 -> {
                                    viewModel.addMovie(movieEntity)
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.movie_not_exist_db),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    binding.buttonAddFavorite.setImageResource(drawable.ic_baseline_playlist_add_check_24)
                                }
                                3 -> {
                                    binding.buttonAddFavorite.setImageResource(drawable.ic_baseline_playlist_add_check_24)
                                }
                                4 -> {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle(getString(R.string.dialog_title2))
                                        .setMessage(getString(R.string.dialog_message2))
                                        .setNegativeButton(getString(R.string.dialog_negative)) { _, _ ->

                                        }
                                        .setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
                                            viewModel.dellMovie(movieEntity)
                                            binding.buttonAddFavorite.setImageResource(drawable.ic_baseline_playlist_add_24)
                                        }
                                        .show()
                                }
                            }
                        }

                        ViewState.Status.FAILURE -> {
                            Toast.makeText(
                                requireContext(),
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
