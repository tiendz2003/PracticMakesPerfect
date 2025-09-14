package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home


import android.R.attr.text
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemContentHeaderBinding
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemContentImageBinding
import com.manutd.ronaldo.practicemakesperfect.databinding.ItemContentTextBinding
import com.manutd.ronaldo.practicemakesperfect.domain.model.NewsSection

class NewsContentAdapter(
    private val onImageClick: (String) -> Unit,
    private val onLinkClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var sections: List<NewsSection> = emptyList()

    fun submitList(newSections: List<NewsSection>) {
        sections = newSections
        notifyDataSetChanged()
    }
    companion object {
        const val TYPE_TEXT = 1
        const val TYPE_IMAGE = 3
        const val TYPE_HEADER = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (sections[position].sectionType) {
            1 -> {
                val hasHeaderMarkup = sections[position].content.markups?.any {
                    it.markupType == 1
                } ?: false
                if (hasHeaderMarkup) TYPE_HEADER else TYPE_TEXT
            }
            3 -> TYPE_IMAGE
            else -> TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT -> {
                val binding = ItemContentTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextViewHolder(binding)
            }

            TYPE_HEADER -> {
                val binding = ItemContentHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }

            TYPE_IMAGE -> {
                val binding = ItemContentImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = sections[position]

        when (holder) {
            is TextViewHolder -> holder.bind(section)
            is HeaderViewHolder -> holder.bind(section)
            is ImageViewHolder -> holder.bind(section)
        }
    }

    override fun getItemCount(): Int = sections.size
    inner class TextViewHolder(private val binding: ItemContentTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(section: NewsSection) {
            val text = section.content.text ?: return

            if (section.content.markups.isNullOrEmpty()) {
                // Plain text
                binding.tvContentText.text = text
            } else {
                // Text with markups
                val spannableString = SpannableString(text)

                section.content.markups.forEach { markup ->
                    val start = markup.start
                    val end = markup.end

                    when (markup.markupType) {
                        1 -> { // Bold
                            spannableString.setSpan(
                                StyleSpan(Typeface.BOLD),
                                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }

                        2 -> { // Italic
                            spannableString.setSpan(
                                StyleSpan(Typeface.ITALIC),
                                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }

                        3 -> { // Underline
                            spannableString.setSpan(
                                UnderlineSpan(),
                                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }

                        4 -> { // Link
                            markup.href?.let { url ->
                                spannableString.setSpan(
                                    object : ClickableSpan() {
                                        override fun onClick(widget: View) {
                                            onLinkClick(url)
                                        }
                                    },
                                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }
                }

                binding.tvContentText.text = spannableString
                binding.tvContentText.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ItemContentHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: NewsSection) {
            binding.tvHeaderText.text = section.content.text
        }
    }

    inner class ImageViewHolder(private val binding: ItemContentImageBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(section: NewsSection) {
            val content = section.content
            binding.ivContentImage.load(section.content.href){
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
            }
            binding.tvImageCaption.text = content.caption ?: ""
            binding.tvImageCaption.visibility = if (content.caption.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
            binding.tvImageCaption.setOnClickListener {
                content.href?.let { url -> onImageClick(url) }
            }
        }
    }

}