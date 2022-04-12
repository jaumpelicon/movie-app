package com.example.movieapp.ui.popularmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.*
import com.example.movieapp.data.adapter.PopularMovieAdapter
import com.example.movieapp.data.network.MovieService
import com.example.movieapp.databinding.FragmentPopularMoviesBinding
import com.example.movieapp.ui.ViewState
import com.example.movieapp.utils.Utils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PopularMoviesFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var binding: FragmentPopularMoviesBinding
    private val viewModel: PopularMoviesViewModel by viewModel {
        val connection = Utils.hasInternetConnection(requireContext())
        val retrofitService = MovieService.getInstance()
        parametersOf(retrofitService, connection)
    }
    private val adapterPopular = PopularMovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_popular_movies, container, false)
        initRecyclerView()
        initObservers()
        popularMoviesScrollListener()
        clickItemRecycler()
        return binding.root
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewPopular.adapter = adapterPopular
        binding.recyclerViewPopular.layoutManager = layoutManager
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState.collect {
                    when (it.status) {
                        ViewState.Status.INITIAL -> {
                            binding.progressBarPopular.visibility = GONE
                            binding.textProgressBarPopular.visibility = GONE
                            binding.popularMoviesTextFeedback.visibility = GONE
                            binding.imageErrorPopular.visibility = GONE
                            binding.recyclerViewPopular.visibility = GONE
                        }

                        ViewState.Status.LOADING -> {
                            binding.progressBarPopular.visibility = VISIBLE
                            binding.textProgressBarPopular.visibility = VISIBLE
                            binding.popularMoviesTextFeedback.visibility = GONE
                            binding.imageErrorPopular.visibility = GONE
                            binding.recyclerViewPopular.visibility = GONE
                        }

                        ViewState.Status.FAILURE -> {
                            binding.progressBarPopular.visibility = GONE
                            binding.textProgressBarPopular.visibility = GONE
                            binding.recyclerViewPopular.visibility = GONE
                            binding.imageErrorPopular.visibility = VISIBLE
                            binding.popularMoviesTextFeedback.visibility = VISIBLE
                            binding.popularMoviesTextFeedback.text = it.message
                        }

                        ViewState.Status.SUCCESS -> {
                            binding.progressBarPopular.visibility = GONE
                            binding.textProgressBarPopular.visibility = GONE
                            binding.popularMoviesTextFeedback.visibility = GONE
                            binding.imageErrorPopular.visibility = GONE
                            binding.recyclerViewPopular.visibility = VISIBLE
                            it.data?.let { movies ->
                                adapterPopular.updateDataMovie(movies)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun popularMoviesScrollListener() {
        binding.recyclerViewPopular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                    viewModel.getMorePopularMovies()
                }
            }
        })
    }

    private fun clickItemRecycler() {
        adapterPopular.itemClick = { movie ->
            findNavController().navigate(
                PopularMoviesFragmentDirections.actionPopularMoviesFragmentToDetailsMovieFragment(
                    movie, true
                )
            )
        }
    }
}
