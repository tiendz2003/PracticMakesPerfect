package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home

import com.manutd.ronaldo.practicemakesperfect.domain.model.Channel
import com.manutd.ronaldo.practicemakesperfect.domain.model.DetailNews
import com.manutd.ronaldo.practicemakesperfect.domain.model.Group
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

sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<Group>) : UiState
    data class Error(val message: String) : UiState
}