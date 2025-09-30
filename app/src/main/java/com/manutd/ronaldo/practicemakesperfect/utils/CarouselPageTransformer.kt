package com.manutd.ronaldo.practicemakesperfect.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CarouselPageTransformer: ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            when{
                position < -1 ->{
                    alpha = 0.5f
                    scaleY = 0.8f
                }
                position <= 1 ->{
                    val scaleFactor = 0.8f.coerceAtLeast(1 - kotlin.math.abs(position) * 0.2f)
                    scaleY = scaleFactor
                    scaleX = scaleFactor

                    // Alpha effect
                    alpha = 0.5f + (1 - kotlin.math.abs(position)) * 0.5f

                    // Elevation effect
                    translationZ = -kotlin.math.abs(position)
                }
                else -> { // (1,+Infinity]
                    alpha = 0.5f
                    scaleY = 0.8f
                }
            }
        }
    }

}