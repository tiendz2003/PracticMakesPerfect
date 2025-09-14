package com.manutd.ronaldo.practicemakesperfect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class DetailNewsResponse(
    @SerialName("description")
    val description: String,
    @SerialName("document_id")
    val documentId: String,
    @SerialName("origin_url")
    val originUrl: String,
    @SerialName("published_date")
    val publishedDate: String,
    @SerialName("publisher")
    val publisher: Publisher,
    @SerialName("sections")
    val sections: List<Section>,
    @SerialName("template_type")
    val templateType: String,
    @SerialName("title")
    val title: String
) {
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

    @Keep
    @Serializable
    data class Section(
        @SerialName("content")
        val content: Content,
        @SerialName("section_type")
        val sectionType: Int
    ) {
        @Keep
        @Serializable

        data class Content(
            @SerialName("caption") val caption: String? = null,
            @SerialName("duration") val duration: Int? = null,
            @SerialName("href") val href: String? = null,
            @SerialName("main_color") val mainColor: String? = null,
            @SerialName("markups") val markups: List<Markup>? = null,
            @SerialName("original_height") val originalHeight: Int? = null,
            @SerialName("original_width") val originalWidth: Int? = null,
            @SerialName("preview_image") val previewImage: PreviewImage? = null,
            @SerialName("text") val text: String? = null
        ) {
            @Keep
            @Serializable
            data class Markup(
                @SerialName("end")
                val end: Int,
                @SerialName("href")
                val href: String?,
                @SerialName("markup_type")
                val markupType: Int,
                @SerialName("start")
                val start: Int
            )

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
    }
}