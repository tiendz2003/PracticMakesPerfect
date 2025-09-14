package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home

import com.manutd.ronaldo.practicemakesperfect.domain.model.DetailNews
import com.manutd.ronaldo.practicemakesperfect.domain.model.News

data class HomeUiState(
    val isLoading: Boolean = false,
    val news: List<News> = emptyList(),
    val error: String? = null
)

data class DetailUiState(
    val isLoading: Boolean = false,
    val detailNews: DetailNews? = null,
    val error: String? = null
)