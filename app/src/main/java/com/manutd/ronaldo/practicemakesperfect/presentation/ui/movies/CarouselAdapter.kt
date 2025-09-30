package com.manutd.ronaldo.practicemakesperfect.presentation.ui.movies

import android.graphics.Movie
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemCarouselImageBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.Channel
import com.manutd.ronaldo.practicemakesperfect.domain.model.Movies

class CarouselAdapter(
    private val onItemClick: (Channel) -> Unit
) : ListAdapter<Channel, CarouselAdapter.ViewHolder>(ChannelDiffCallback()) {

    inner class ViewHolder(
        private val binding: ItemCarouselImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(channel: Channel) {
            Log.d("CarouselAdapter", "Binding channel: ${channel.logoUrl}")
            binding.imageThumbnail.load(
                channel.logoUrl
            ){
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
                fallback(R.drawable.ic_launcher_foreground)
                transformations(
                    RoundedCornersTransformation(10f)
                )
            }
            binding.root.setOnClickListener {
                onItemClick(channel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCarouselImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class ChannelDiffCallback : DiffUtil.ItemCallback<Channel>() {
    override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
        return oldItem == newItem
    }
}