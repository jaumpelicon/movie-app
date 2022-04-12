package com.example.movieapp.ui.searchmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.*
import com.example.movieapp.data.adapter.SearchMovieAdapter
import com.example.movieapp.data.network.MovieService
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.ui.ViewState
import com.example.movieapp.utils.Utils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchMoviesFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchMoviesViewModel by viewModel {
        val connection = Utils.hasInternetConnection(requireContext())
        val retrofitService = MovieService.getInstance()
        parametersOf(retrofitService, connection)
    }
    private val adapterSearch = SearchMovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        initRecyclerView()
        searchMoviesScrollListener()
        initObservers()
        binding.searchMovieButton.setOnClickListener {
            viewModel.movieName = binding.movieNameEditText.text.toString()
            if (viewModel.movieName.isNotEmpty()) {
                binding.textFeedbackSearch.visibility = GONE
                binding.progressBarSearch.visibility = VISIBLE
                binding.textProgressBarSearch.visibility = VISIBLE
                viewModel.adultFilter = true
                if (binding.checkboxAdultFilter.isChecked) {
                    viewModel.adultFilter = false
                }
                viewModel.getFirstSearchMovies()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_movie_null),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        clickRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSearch.adapter = adapterSearch
        binding.recyclerViewSearch.layoutManager = layoutManager
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState.collect {
                    when (it.status) {
                        ViewState.Status.INITIAL -> {
                            binding.progressBarSearch.visibility = GONE
                            binding.textProgressBarSearch.visibility = GONE
                            binding.textFeedbackSearch.visibility = GONE
                            binding.imageErrorSearch.visibility = GONE
                            binding.recyclerViewSearch.visibility = GONE
                        }

                        ViewState.Status.LOADING -> {
                            binding.textFeedbackSearch.visibility = GONE
                            binding.imageErrorSearch.visibility = GONE
                            binding.recyclerViewSearch.visibility = GONE
                            binding.progressBarSearch.visibility = VISIBLE
                            binding.textProgressBarSearch.visibility = VISIBLE
                        }

                        ViewState.Status.FAILURE -> {
                            binding.progressBarSearch.visibility = GONE
                            binding.textProgressBarSearch.visibility = GONE
                            binding.recyclerViewSearch.visibility = GONE
                            binding.textFeedbackSearch.visibility = VISIBLE
                            binding.imageErrorSearch.visibility = VISIBLE
                            binding.textFeedbackSearch.text = it.message
                        }

                        ViewState.Status.SUCCESS -> {
                            binding.progressBarSearch.visibility = GONE
                            binding.textProgressBarSearch.visibility = GONE
                            binding.textFeedbackSearch.visibility = GONE
                            binding.imageErrorSearch.visibility = GONE
                            binding.recyclerViewSearch.visibility = VISIBLE
                            it.data?.let { movies ->
                                if (movies.isNotEmpty()) {
                                    adapterSearch.updateDataMovie(movies)
                                } else {
                                    binding.textFeedbackSearch.visibility = VISIBLE
                                    binding.textFeedbackSearch.text =
                                        getString(R.string.feedback_search_null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun searchMoviesScrollListener() {
        binding.recyclerViewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                    viewModel.getMoreSearchMovies()
                }
            }
        })
    }

    private fun clickRecyclerView() {
        adapterSearch.itemClick = { movie ->
            findNavController().navigate(
                SearchMoviesFragmentDirections.actionSearchMoviesFragmentToDetailsMovieFragment2(
                    movie, true
                )
            )
        }
    }
}

