package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home

import android.R.attr.headerBackground
import android.R.attr.text
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat.isNestedScrollingEnabled
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.FragmentDetailBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.DetailNews
import com.manutd.ronaldo.practicemakesperfect.utils.toFormattedDate
import com.skydoves.transformationlayout.TransformationLayout
import com.skydoves.transformationlayout.onTransformationEndContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.idn.binarySearch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentAdapter: NewsContentAdapter
    private val viewModels: DetailNewsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModels.uiState.flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            ).collect { uiState ->
                Log.d("DetailFragment", "uiState: $uiState")
               uiState.detailNews?.let { detailNews->
                   with(binding) {
                       collapsingToolbar.title = detailNews.title
                       val firstImageSection = detailNews.sections.find { it.sectionType == 3 }
                       firstImageSection?.content?.href?.let { imageUrl ->
                           ivHeaderBackground.load(imageUrl) {
                               crossfade(true)
                               placeholder(R.drawable.ic_launcher_foreground)
                               error(R.drawable.ic_launcher_foreground)
                           }
                       }
                       tvTitle.text = detailNews.title
                       tvDescription.text = detailNews.description
                       tvPublisher.text = detailNews.publisher.name
                       tvPublishedDate.text = detailNews.publishedDate.toFormattedDate()
                       // Lọc và cập nhật dữ liệu cho RecyclerView
                       val contentSections = detailNews.sections.filterNot { section ->
                           section.sectionType == 1 && section.content.text?.startsWith("Theo") == true
                       }
                       // Cập nhật danh sách cho adapter
                       contentAdapter.submitList(contentSections)
                       val lastSection = detailNews.sections.lastOrNull()
                       if (lastSection?.sectionType == 1 && lastSection.content.text?.startsWith("Theo") == true) {
                           tvSourceAttribution.text = lastSection.content.text
                       }
                   }
               }

            }
        }
    }

    private fun setupRecyclerView() {
        // Khởi tạo adapter với danh sách rỗng ban đầu
        contentAdapter = NewsContentAdapter(
            onImageClick = { imageUrl ->
                // Xử lý sự kiện click ảnh
            },
            onLinkClick = { url ->
                // Xử lý sự kiện click link
            }
        )

        binding.rvContentSections.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contentAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvContentSections.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val TAG = "DetailFragment"
        const val newsId = "newsId"

    }
}