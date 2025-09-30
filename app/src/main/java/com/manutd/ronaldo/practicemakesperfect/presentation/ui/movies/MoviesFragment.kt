package com.manutd.ronaldo.practicemakesperfect.presentation.ui.movies

import android.graphics.Rect
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.FragmentMoviesBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.Channel
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.home.UiState
import com.manutd.ronaldo.practicemakesperfect.utils.CarouselPageTransformer
import com.manutd.ronaldo.practicemakesperfect.utils.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesFragment : Fragment() {
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeAdapter: HomeChannelAdapter
    private val viewModel: MoviesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeData()
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeChannelAdapter { channel ->
            // Handle item click
            navigateToDetail(channel)
        }

        binding.recyclerHome.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(requireContext())

            // Performance optimizations
            setHasFixedSize(false) // Because items have different heights
            itemAnimator = null // Disable animations for better scrolling

            // Prefetch for smoother scrolling
            (layoutManager as? LinearLayoutManager)?.apply {
                initialPrefetchItemCount = 4
            }

            // Add spacing between groups
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.getChildAdapterPosition(view) != 0) {
                        outRect.top = 16.dpToPx()
                    }
                }
            })

        }
    }

    private fun setupSwipeRefresh() {
        binding.refreshHome.setOnRefreshListener {
            viewModel.fetchMovies()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.refreshHome.isRefreshing = true
                    }

                    is UiState.Success -> {
                        binding.refreshHome.isRefreshing = false
                        Log.d("MoviesFragment", "Data received: ${state.data}")
                        homeAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        binding.refreshHome.isRefreshing = false
                        // Show error
                    }
                }
            }
        }
    }

    private fun navigateToDetail(channel: Channel) {
        // Navigate to detail screen
    }


    /*private fun setupAutoScroll() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = binding.pagerTop.currentItem
                val itemCount = binding.pagerTop.adapter?.itemCount ?: 0

                if (itemCount > 0) {
                    val nextItem = (currentItem + 1) % itemCount
                    binding.pagerTop.setCurrentItem(nextItem, true)
                }

                handler.postDelayed(this, 3000) // 3 seconds
            }
        }
        handler.postDelayed(runnable, 3000)
    }*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}