package com.manutd.ronaldo.practicemakesperfect.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class News(
    val documentId: String,
    val title: String,
    val publisher: String,
    val publishedDate: String,
    val imageUrl: List<String>?
) : Parcelable