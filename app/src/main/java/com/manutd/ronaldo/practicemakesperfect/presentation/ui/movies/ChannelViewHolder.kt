package com.manutd.ronaldo.practicemakesperfect.presentation.ui.movies

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemCarouselBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.Channel
import com.manutd.ronaldo.practicemakesperfect.domain.model.Group
import com.manutd.ronaldo.practicemakesperfect.utils.CarouselPageTransformer
import com.manutd.ronaldo.practicemakesperfect.utils.dpToPx

sealed class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(group: Group)
    class SliderViewHolder(
        val onItemClick:(Channel) -> Unit,
        private val binding: ItemCarouselBinding
    ) : ChannelViewHolder(binding.root) {
        private val carouselAdapter = CarouselAdapter(onItemClick)

        init {
            setupViewPager()
        }

        override fun bind(group: Group) {
            carouselAdapter.submitList(group.channels)

            binding.indicatorHome.attachTo(binding.pagerTop)

            startAutoScroll()
        }

        private fun setupViewPager() {
            binding.pagerTop.apply {
                adapter = carouselAdapter
                offscreenPageLimit = 1

                val compositeTransformer = CompositePageTransformer().apply {
                    addTransformer(MarginPageTransformer(16.dpToPx()))
                    addTransformer(CarouselPageTransformer())
                }
                setPageTransformer(compositeTransformer)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        updateBlurBackground(position)
                        updateMovieInfo(position)
                    }
                })
            }
        }

        private fun updateBlurBackground(position: Int) {
            val channel = carouselAdapter.currentList.getOrNull(position) ?: return

            binding.blurView.load(channel.logoUrl)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val blurEffect = RenderEffect.createBlurEffect(
                    25f, 25f, Shader.TileMode.CLAMP)
                binding.blurView.setRenderEffect(blurEffect)
            }
        }

        private fun updateMovieInfo(position: Int) {
            val channel = carouselAdapter.currentList.getOrNull(position) ?: return

            binding.textTitle.text = channel.name
            binding.textTitleEnglish.text = "Tiêu đề tiếng Anh "
            binding.textDescription.text = channel.description
            binding.textImdb.text = "7.0"
            binding.textQuality.text = "HD"
            binding.textRate.text = "7.0"
            binding.textYear.text = "2025"
            binding.textEpisode.text = "E.12"
        }

        private fun startAutoScroll() {

        }
    }
}

