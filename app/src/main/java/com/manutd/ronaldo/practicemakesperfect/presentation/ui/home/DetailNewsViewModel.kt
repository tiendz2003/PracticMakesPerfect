package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home

import android.R.id.message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manutd.ronaldo.practicemakesperfect.domain.repository.NewsRepository
import com.manutd.ronaldo.practicemakesperfect.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        getDetailNews()
    }

    private fun updateState(newState: (DetailUiState) -> DetailUiState) {
        _uiState.update(newState)
    }

    private fun getDetailNews() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            when (val result = repository.getDetailNews()) {
                is Resource.Success -> {
                    updateState { it.copy(detailNews = result.data, isLoading = false) }
                }

                is Resource.Error -> {
                    updateState { it.copy(error = result.message.message, isLoading = false) }
                }

                Resource.Loading -> {
                    updateState { it.copy(isLoading = true) }
                }
            }
        }
    }
}