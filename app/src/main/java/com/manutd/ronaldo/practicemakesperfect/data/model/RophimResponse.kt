package com.manutd.ronaldo.practicemakesperfect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class RophimResponse(
    @SerialName("color")
    val color: String,
    @SerialName("description")
    val description: String,
    @SerialName("grid_number")
    val gridNumber: Int,
    @SerialName("groups")
    val groups: List<Group>,
    @SerialName("id")
    val id: String,
    @SerialName("image")
    val image: Image,
    @SerialName("name")
    val name: String,
    @SerialName("org_metadata")
    val orgMetadata: OrgMetadata,
    @SerialName("search")
    val search: Search,
    @SerialName("url")
    val url: String
) {
    @Keep
    @Serializable
    data class Group(
        @SerialName("channels")
        val channels: List<Channel>,
        @SerialName("display")
        val display: String,
        @SerialName("enable_detail")
        val enableDetail: Boolean,
        @SerialName("grid_number")
        val gridNumber: Int?,
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String?,
        @SerialName("remote_data")
        val remoteData: RemoteData?
    ) {
        @Keep
        @Serializable
        data class Channel(
            @SerialName("description")
            val description: String,
            @SerialName("display")
            val display: String,
            @SerialName("enable_detail")
            val enableDetail: Boolean,
            @SerialName("id")
            val id: String,
            @SerialName("image")
            val image: Image,
            @SerialName("name")
            val name: String,
            @SerialName("org_metadata")
            val orgMetadata: OrgMetadata,
            @SerialName("remote_data")
            val remoteData: RemoteData,
            @SerialName("share")
            val share: Share,
            @SerialName("type")
            val type: String
        ) {
            @Keep
            @Serializable
            data class Image(
                @SerialName("height")
                val height: Int,
                @SerialName("type")
                val type: String,
                @SerialName("url")
                val url: String,
                @SerialName("width")
                val width: Int
            )

            @Keep
            @Serializable
            data class OrgMetadata(
                @SerialName("description")
                val description: String,
                @SerialName("image")
                val image: String,
                @SerialName("title")
                val title: String
            )

            @Keep
            @Serializable
            data class RemoteData(
                @SerialName("url")
                val url: String
            )

            @Keep
            @Serializable
            data class Share(
                @SerialName("url")
                val url: String
            )
        }

        @Keep
        @Serializable
        data class RemoteData(
            @SerialName("url")
            val url: String
        )
    }

    @Keep
    @Serializable
    data class Image(
        @SerialName("height")
        val height: Int,
        @SerialName("type")
        val type: String,
        @SerialName("url")
        val url: String,
        @SerialName("width")
        val width: Int
    )

    @Keep
    @Serializable
    data class OrgMetadata(
        @SerialName("description")
        val description: String,
        @SerialName("image")
        val image: String,
        @SerialName("title")
        val title: String
    )

    @Keep
    @Serializable
    data class Search(
        @SerialName("paging")
        val paging: Paging,
        @SerialName("search_key")
        val searchKey: String,
        @SerialName("url")
        val url: String
    ) {
        @Keep
        @Serializable
        data class Paging(
            @SerialName("page_key")
            val pageKey: String,
            @SerialName("size_key")
            val sizeKey: String
        )
    }
}