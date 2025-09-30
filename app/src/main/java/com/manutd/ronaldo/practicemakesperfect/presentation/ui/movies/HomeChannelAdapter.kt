package com.manutd.ronaldo.practicemakesperfect.presentation.ui.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemCarouselBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.Channel
import com.manutd.ronaldo.practicemakesperfect.domain.model.ChannelType
import com.manutd.ronaldo.practicemakesperfect.domain.model.Group

class HomeChannelAdapter(
    private val onItemClick: (Channel) -> Unit
) : ListAdapter<Group, ChannelViewHolder>(GroupDiffCallback()) {

    // RecycledViewPool for optimization
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            ChannelType.SLIDER -> VIEW_TYPE_SLIDER
            ChannelType.HORIZONTAL -> VIEW_TYPE_HORIZONTAL
            ChannelType.TOP -> VIEW_TYPE_TOP
            else -> VIEW_TYPE_HORIZONTAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        return when (viewType) {
            VIEW_TYPE_SLIDER -> {
                val binding = ItemCarouselBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ChannelViewHolder.SliderViewHolder(onItemClick, binding)
            }
            /* VIEW_TYPE_HORIZONTAL -> {
                *//* val binding = ItemChannelHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ChannelViewHolder.HorizontalViewHolder(binding, onItemClick).apply {
                    // Share RecycledViewPool for better performance
                    itemView.findViewById<RecyclerView>(R.id.recyclerChannels)
                        ?.setRecycledViewPool(viewPool)
                }*//*
            }
            VIEW_TYPE_TOP -> {
                val binding = ItemChannelTopBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ChannelViewHolder.TopViewHolder(binding, onItemClick).apply {
                    itemView.findViewById<RecyclerView>(R.id.recyclerTopChannels)
                        ?.setRecycledViewPool(viewPool)
                }
            }*/
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private const val VIEW_TYPE_SLIDER = 0
        private const val VIEW_TYPE_HORIZONTAL = 1
        private const val VIEW_TYPE_TOP = 2
    }
}
class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }
}