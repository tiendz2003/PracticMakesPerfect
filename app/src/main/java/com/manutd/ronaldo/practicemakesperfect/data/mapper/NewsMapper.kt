package com.manutd.ronaldo.practicemakesperfect.data.mapper

import com.manutd.ronaldo.practicemakesperfect.data.model.NewsResponse
import com.manutd.ronaldo.practicemakesperfect.domain.model.News
import com.manutd.ronaldo.practicemakesperfect.utils.toFormattedDate
import com.manutd.ronaldo.practicemakesperfect.data.model.DetailNewsResponse
import com.manutd.ronaldo.practicemakesperfect.domain.model.*

fun NewsResponse.Item.toNews(): News {
    return News(
        documentId = documentId,
        title = title,
        publisher = publisher.name,
        publishedDate = publishedDate.toFormattedDate(),
        imageUrl = images?.map { it.href } ?: emptyList()
    )
}

fun NewsResponse.toDomain(): List<News> {
    return items.map { it.toNews() }
}


internal fun DetailNewsResponse.toDomain(): DetailNews {
    return DetailNews(
        documentId = documentId,
        title = title,
        description = description,
        publishedDate = publishedDate,
        originUrl = originUrl,
        publisher = publisher.toDomain(),
        templateType = templateType,
        sections = sections.map { it.toDomain() }
    )
}

private fun DetailNewsResponse.Publisher.toDomain() = Publisher(
    id = id,
    name = name,
    icon = icon
)

private fun DetailNewsResponse.Section.toDomain() = NewsSection(
    sectionType = sectionType,
    content = content.toDomain()
)

private fun DetailNewsResponse.Section.Content.toDomain() = SectionContent(
    text = text,
    markups = markups?.map { it.toDomain() },
    href = href,
    caption = caption,
    duration = duration,
    previewImage = previewImage?.toDomain(),
    mainColor = mainColor,
    originalWidth = originalWidth,
    originalHeight = originalHeight,
    width = previewImage?.width,
    height = previewImage?.height
)

private fun DetailNewsResponse.Section.Content.Markup.toDomain() = Markup(
    markupType = markupType,
    start = start,
    end = end,
    href = href
)

private fun DetailNewsResponse.Section.Content.PreviewImage.toDomain() = PreviewImage(
    href = href,
    mainColor = mainColor,
    width = width,
    height = height
)
