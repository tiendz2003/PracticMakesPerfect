package com.manutd.ronaldo.practicemakesperfect.presentation.theme

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.button.MaterialButton
import com.manutd.ronaldo.practicemakesperfect.R
import java.lang.reflect.Array.getInt

class DefaultButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {
    init {
        initButton(attrs)
        defaultSetup()
    }

    fun defaultSetup() {
        val paddingHorizontal = context.resources.getDimensionPixelSize(R.dimen.button_padding)
        val paddingVertical = context.resources.getDimensionPixelSize(R.dimen.button_padding)
        setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
        gravity = Gravity.CENTER
    }

    fun initButton(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.Button) {
                val buttonStyle = getInt(R.styleable.Button_ButtonStyle, 0)
                applyButtonStyle(buttonStyle)
            }
        }
    }

    private fun applyButtonStyle(style: Int) {
        when (style) {
            0 -> { // Primary
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.primaryColor)
                setTextColor(ContextCompat.getColor(context, R.color.white))
                typeface = resources.getFont(R.font.bold)
            }

            1 -> { // Secondary
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.teal_700)
                setTextColor(ContextCompat.getColor(context, R.color.white))
                typeface = resources.getFont(R.font.medium)
            }

            2 -> { // Outline
                backgroundTintList =
                    ContextCompat.getColorStateList(context, android.R.color.transparent)
                strokeColor = ContextCompat.getColorStateList(context, R.color.primaryColor)
                strokeWidth =
                    context.resources.getDimensionPixelSize(R.dimen.button_stroke_width)
                setTextColor(ContextCompat.getColor(context, R.color.white))
                typeface = resources.getFont(R.font.bold)
            }

            else -> {
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.primaryColor)
                setTextColor(ContextCompat.getColor(context, R.color.white))
                typeface = resources.getFont(R.font.bold)
            }
        }
    }
}