package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemGridNewsBinding
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemOneImgNewsBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.News
import com.skydoves.transformationlayout.TransformationLayout

private const val VIEW_TYPE_ONE_IMAGE = 1
private const val VIEW_TYPE_GRID = 2

class NewsAdapter (
    private val onItemClick: (News, TransformationLayout) -> Unit
): ListAdapter<News, RecyclerView.ViewHolder>(diffUtil) {
    inner class ImageViewHolder(private val binding: ItemOneImgNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: News) {
            Log.d("NewsAdapter", "Binding news: ${news.title}")
            Log.d("NewsAdapter", " URLs: ${news.imageUrl}")
            Log.d("NewsAdapter", " count: ${news.imageUrl?.size}")
            binding.tvTitle.text = news.title
            binding.tvTimestamp.text = news.publishedDate
            binding.tvAuthor.text = news.publisher
            val imageUrl = news.imageUrl?.firstOrNull()
            Log.d("NewsAdapter", "URL: $imageUrl")
            binding.ivImage.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground) // Ảnh tạm
                error(R.drawable.ic_launcher_foreground)
                listener(
                    onStart = { Log.d("NewsAdapter", "đang tải") },
                    onSuccess = { _, _ -> Log.d("NewsAdapter", "taỉ thành công") },
                    onError = { _, throwable ->
                        Log.e(
                            "NewsAdapter",
                            "tải lỗi${throwable.throwable}"
                        )
                    }
                )
            }
        }

    }

    inner class ImagesViewHolder(private val binding: ItemGridNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val imageViews = listOf(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3
        )

        fun bind(news: News) {
            with(binding) {
                transformationLayout.transitionName = "news_transition"
                tvTitle.text = news.title
                tvAuthor.text = news.publisher
                tvTimestamp.text = news.publishedDate
                imageViews.forEach { it.visibility = View.GONE }
                flImage3.visibility = View.GONE
                tvMoreImages.visibility = View.GONE
                viewOverlay.visibility = View.GONE
                itemView.setOnClickListener {
                    onItemClick(news, transformationLayout)
                }
            }

            val imageCount = news.imageUrl?.size ?: 0
            Log.d("NewsAdapter", " $imageCount")

            if (imageCount == 0) return

            // Hiển thị tối đa 3 ảnh
            for (i in 0 until minOf(imageCount, 3)) {
                val imageView = imageViews[i]
                val imageUrl = news.imageUrl?.get(i)

                Log.d("NewsAdapter", "Loading $i: $imageUrl")

                imageView.visibility = View.VISIBLE
                if (i == 2) binding.flImage3.visibility =
                    View.VISIBLE // Hiển thị FrameLayout cho ảnh thứ 3

                imageView.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                    listener(
                        onError = { _, throwable ->
                            Log.e("NewsAdapter", " lỗi: ${throwable.throwable}")
                        }
                    )
                }
            }

            if (imageCount > 3) {
                binding.viewOverlay.visibility = View.VISIBLE
                binding.tvMoreImages.visibility = View.VISIBLE
                binding.tvMoreImages.text = "+${imageCount - 3}"
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val news = getItem(position)
        return if (news.imageUrl?.size == 1) {
            VIEW_TYPE_ONE_IMAGE
        } else {
            VIEW_TYPE_GRID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ONE_IMAGE -> {
                val binding = ItemOneImgNewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageViewHolder(binding)

            }

            VIEW_TYPE_GRID -> {
                val binding = ItemGridNewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImagesViewHolder(binding)
            }

            else -> throw IllegalArgumentException("ViewType không đúng")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val news = getItem(position)
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(news)
            }

            is ImagesViewHolder -> {
                holder.bind(news)
            }
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem.documentId == newItem.documentId
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }
}
