package com.manutd.ronaldo.practicemakesperfect.domain.model


data class Movies(
    val color: String,
    val description: String,
    val gridNumber: Int,
    val groups: List<Group>,
    val id: String,
    val image: String,
    val name: String,
)

data class Group(
    val id: String,
    val display: String,
    val name: String?,
    val channels: List<Channel>,
    val remoteUrl: String? ,
    val type: ChannelType = ChannelType.UNKNOWN,
    // từ Group.RemoteData.url
)

data class Channel(
    val id: String,
    val name: String,
    val display: String,
    val description: String,
    val logoUrl: String,        // từ Channel.Image.url
    val streamUrl: String,      // từ Channel.RemoteData.url
    val shareUrl: String        // từ Channel.Share.url
)
enum class ChannelType {
    HORIZONTAL,
    SLIDER,
    TOP,
    UNKNOWN
}
