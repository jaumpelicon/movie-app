package com.example.movieapp.ui.topratedmovies

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
import com.example.movieapp.data.adapter.RatedMovieAdapter
import com.example.movieapp.data.network.MovieService
import com.example.movieapp.databinding.FragmentRatedMoviesBinding
import com.example.movieapp.ui.ViewState
import com.example.movieapp.utils.Utils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RatedMoviesFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var binding: FragmentRatedMoviesBinding
    private val viewModel: RatedMoviesViewModel by viewModel {
        val connection = Utils.hasInternetConnection(requireContext())
        val retrofitService = MovieService.getInstance()
        parametersOf(retrofitService, connection)
    }
    private val adapterRated = RatedMovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rated_movies, container, false)
        initRecyclerView()
        ratedMoviesScrollListener()
        initObservers()
        clickItemRecycler()
        return binding.root
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRated.adapter = adapterRated
        binding.recyclerViewRated.layoutManager = layoutManager
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState.collect {
                    when (it.status) {
                        ViewState.Status.INITIAL -> {
                            binding.progressBarRated.visibility = GONE
                            binding.textProgressBarRated.visibility = GONE
                            binding.moviesRatedFeedback.visibility = GONE
                            binding.imageErrorRated.visibility = GONE
                            binding.recyclerViewRated.visibility = GONE
                        }

                        ViewState.Status.LOADING -> {
                            binding.moviesRatedFeedback.visibility = GONE
                            binding.imageErrorRated.visibility = GONE
                            binding.recyclerViewRated.visibility = GONE
                            binding.progressBarRated.visibility = VISIBLE
                            binding.textProgressBarRated.visibility = VISIBLE
                        }

                        ViewState.Status.FAILURE -> {
                            binding.recyclerViewRated.visibility = GONE
                            binding.progressBarRated.visibility = GONE
                            binding.textProgressBarRated.visibility = GONE
                            binding.moviesRatedFeedback.visibility = VISIBLE
                            binding.imageErrorRated.visibility = VISIBLE
                            binding.moviesRatedFeedback.text = it.message
                        }

                        ViewState.Status.SUCCESS -> {
                            binding.progressBarRated.visibility = GONE
                            binding.textProgressBarRated.visibility = GONE
                            binding.moviesRatedFeedback.visibility = GONE
                            binding.imageErrorRated.visibility = GONE
                            binding.recyclerViewRated.visibility = VISIBLE
                            it.data?.let { movies ->
                                adapterRated.updateDataMovie(movies)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun ratedMoviesScrollListener() {
        binding.recyclerViewRated.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                    viewModel.getMoreRatedMovies()
                }
            }
        })
    }

    private fun clickItemRecycler() {
        adapterRated.itemClick = { movie ->
            findNavController().navigate(
                RatedMoviesFragmentDirections.actionRatedMoviesFragmentToDetailsMovieFragment(
                    movie, true
                )
            )
        }
    }
}



