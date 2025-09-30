package com.manutd.ronaldo.practicemakesperfect.data.mapper

import android.R.attr.type
import com.manutd.ronaldo.practicemakesperfect.data.model.RophimResponse
import com.manutd.ronaldo.practicemakesperfect.domain.model.Channel
import com.manutd.ronaldo.practicemakesperfect.domain.model.ChannelType
import com.manutd.ronaldo.practicemakesperfect.domain.model.Group
import com.manutd.ronaldo.practicemakesperfect.domain.model.Movies

fun RophimResponse.toDomain(): Movies {
    return Movies(
        color = color,
        description = description,
        gridNumber = gridNumber,
        groups = groups.map { it.toDomain() },
        id = id,
        image = image.url,
        name = name
    )
}
data class SectionMovies(
    val sliderMovies: Group,
    val horizontalMovies:List<Group>,
    val topMovies:List<Group>
)
fun RophimResponse.Group.toDomain(): Group {
    val type = when(display.lowercase()){
        "horizontal" -> {
            if (name?.contains("top", ignoreCase = true) == true) {
                ChannelType.TOP
            } else {
                ChannelType.HORIZONTAL
            }
        }
        "slider" -> ChannelType.SLIDER
        else -> ChannelType.UNKNOWN
    }
    return Group(
        id = id,
        display = display,
        name = name,
        channels = channels.map { it.toDomain() },
        remoteUrl = remoteData?.url,
        type = type
    )
}

fun RophimResponse.Group.Channel.toDomain(): Channel {
    return Channel(
        id = id,
        name = name,
        display = display,
        description = description,
        logoUrl = image.url,
        streamUrl = remoteData.url,
        shareUrl = share.url
    )
}