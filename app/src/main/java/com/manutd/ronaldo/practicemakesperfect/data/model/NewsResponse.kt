package com.manutd.ronaldo.practicemakesperfect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class NewsResponse(
    @SerialName("items")
    val items: List<Item>
) {
    @Keep
    @Serializable
    data class Item(
        @SerialName("avatar")
        val avatar: Avatar?,
        @SerialName("content")
        val content: Content?,
        @SerialName("content_type")
        val contentType: String,
        @SerialName("description")
        val description: String,
        @SerialName("document_id")
        val documentId: String,
        @SerialName("images")
        val images: List<Image>?,
        @SerialName("origin_url")
        val originUrl: String,
        @SerialName("published_date")
        val publishedDate: String,
        @SerialName("publisher")
        val publisher: Publisher,
        @SerialName("title")
        val title: String
    ) {
        @Keep
        @Serializable
        data class Avatar(
            @SerialName("height")
            val height: Int,
            @SerialName("href")
            val href: String,
            @SerialName("main_color")
            val mainColor: String,
            @SerialName("width")
            val width: Int
        )

        @Keep
        @Serializable
        data class Content(
            @SerialName("duration")
            val duration: Int,
            @SerialName("href")
            val href: String,
            @SerialName("preview_image")
            val previewImage: PreviewImage
        ) {
            @Keep
            @Serializable
            data class PreviewImage(
                @SerialName("height")
                val height: Int,
                @SerialName("href")
                val href: String,
                @SerialName("main_color")
                val mainColor: String,
                @SerialName("width")
                val width: Int
            )
        }

        @Keep
        @Serializable
        data class Image(
            @SerialName("height")
            val height: Int,
            @SerialName("href")
            val href: String,
            @SerialName("main_color")
            val mainColor: String,
            @SerialName("width")
            val width: Int
        )

        @Keep
        @Serializable
        data class Publisher(
            @SerialName("icon")
            val icon: String,
            @SerialName("id")
            val id: String,
            @SerialName("name")
            val name: String
        )
    }
}