package com.manutd.ronaldo.practicemakesperfect.presentation.theme

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.textview.MaterialTextView
import com.manutd.ronaldo.practicemakesperfect.R

class DefaultTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialTextView(context, attrs, defStyleAttr) {
    companion object {
        const val TEXT_STYLE_REGULAR = 0
        const val TEXT_STYLE_MEDIUM = 1
        const val TEXT_STYLE_SEMIBOLD = 2
        const val TEXT_STYLE_BOLD = 3
    }

    init {
        initView(attrs)
    }

    fun initView(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.DefaultTextView) {
                val textStyle = getInt(
                    R.styleable.DefaultTextView_TextStyle,
                    TEXT_STYLE_REGULAR
                )
                applyStyle(textStyle)
            }
        }
    }

    fun applyStyle(textStyle: Int) {
        val typeface = when (textStyle) {
            TEXT_STYLE_MEDIUM -> ResourcesCompat.getFont(context, R.font.medium)
            TEXT_STYLE_SEMIBOLD -> ResourcesCompat.getFont(context, R.font.semibold)
            TEXT_STYLE_BOLD -> ResourcesCompat.getFont(context, R.font.bold)
            else -> ResourcesCompat.getFont(context, R.font.regular)
        }
        this.typeface = typeface
    }

    fun setTextStyle(textStyle: Int) {
        applyStyle(textStyle)
    }
}