package com.example.movieapp.ui.upcomingmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.*
import com.example.movieapp.data.adapter.UpcomingMovieAdapter
import com.example.movieapp.data.network.MovieService
import com.example.movieapp.databinding.FragmentUpcomingMoviesBinding
import com.example.movieapp.ui.ViewState
import com.example.movieapp.utils.Utils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UpcomingMoviesFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingMoviesBinding
    private lateinit var layoutManager: LinearLayoutManager
    private val moviesViewModel: UpcomingMoviesViewModel by viewModel {
        val connection = Utils.hasInternetConnection(requireContext())
        val retrofitService = MovieService.getInstance()
        parametersOf(retrofitService, connection)
    }
    private val adapterUpcoming = UpcomingMovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming_movies, container, false)
        initRecyclerView()
        upcomingMoviesScrollListener()
        initObservers()
        clickItemRecycler()
        return binding.root
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewUpcoming.adapter = adapterUpcoming
        binding.recyclerViewUpcoming.layoutManager = layoutManager
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                moviesViewModel.movieState.collect {
                    when (it.status) {
                        ViewState.Status.INITIAL -> {
                            binding.progressBarUpcoming.visibility = GONE
                            binding.textProgressBarUpcoming.visibility = GONE
                            binding.upcomingMoviesTextFeedback.visibility = GONE
                            binding.imageErrorUpcoming.visibility = GONE
                            binding.recyclerViewUpcoming.visibility = GONE
                        }

                        ViewState.Status.LOADING -> {
                            binding.upcomingMoviesTextFeedback.visibility = GONE
                            binding.imageErrorUpcoming.visibility = GONE
                            binding.recyclerViewUpcoming.visibility = GONE
                            binding.progressBarUpcoming.visibility = VISIBLE
                            binding.textProgressBarUpcoming.visibility = VISIBLE
                        }

                        ViewState.Status.FAILURE -> {
                            binding.progressBarUpcoming.visibility = GONE
                            binding.textProgressBarUpcoming.visibility = GONE
                            binding.recyclerViewUpcoming.visibility = GONE
                            binding.upcomingMoviesTextFeedback.visibility = VISIBLE
                            binding.imageErrorUpcoming.visibility = VISIBLE
                            binding.upcomingMoviesTextFeedback.text = it.message
                        }

                        ViewState.Status.SUCCESS -> {
                            binding.progressBarUpcoming.visibility = GONE
                            binding.textProgressBarUpcoming.visibility = GONE
                            binding.upcomingMoviesTextFeedback.visibility = GONE
                            binding.imageErrorUpcoming.visibility = GONE
                            binding.recyclerViewUpcoming.visibility = VISIBLE
                            it.data?.let { movies ->
                                adapterUpcoming.updateDataMovie(movies)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun upcomingMoviesScrollListener() {
        binding.recyclerViewUpcoming.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                    moviesViewModel.getMoreUpcomingMovies()
                }
            }
        })
    }

    private fun clickItemRecycler() {
        adapterUpcoming.itemClick = { movie ->
            findNavController().navigate(
                UpcomingMoviesFragmentDirections.actionUpcomingMoviesFragmentToDetailsMovieFragment(
                    movie, true
                )
            )
        }
    }
}

