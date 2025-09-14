package com.manutd.ronaldo.practicemakesperfect.domain.model

data class DetailNews(
    val documentId: String,
    val title: String,
    val description: String,
    val publishedDate: String,
    val originUrl: String,
    val publisher: Publisher,
    val templateType: String,
    val sections: List<NewsSection>
)

data class Publisher(
    val id: String,
    val name: String,
    val icon: String?
)

data class NewsSection(
    val sectionType: Int,
    val content: SectionContent
)

data class SectionContent(
    val text: String? = null,
    val markups: List<Markup>? = null,
    val href: String? = null,
    val caption: String? = null,
    val duration: Int? = null,
    val previewImage: PreviewImage? = null,
    val mainColor: String? = null,
    val originalWidth: Int? = null,
    val originalHeight: Int? = null,
    val width: Int? = null,
    val height: Int? = null
)

data class Markup(
    val markupType: Int,
    val start: Int,
    val end: Int,
    val href: String? = null
)

data class PreviewImage(
    val href: String,
    val mainColor: String,
    val width: Int,
    val height: Int
)