package com.manutd.ronaldo.practicemakesperfect.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manutd.ronaldo.practicemakesperfect.domain.repository.NewsRepository
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.home.HomeUiState
import com.manutd.ronaldo.practicemakesperfect.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getNews()
    }

    private fun updateState(newState: (HomeUiState) -> HomeUiState) {
        _uiState.update(newState)
    }

    fun getNews() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val result = newsRepository.getNews()
            when (result) {
                is Resource.Success -> {
                    Log.d("HomeViewModel", "getNews: ${result.data}")
                    updateState { it.copy(news = result.data, isLoading = false) }
                }

                is Resource.Error -> updateState {
                    Log.e("HomeViewModel", "getNews: ${result.message.message}")
                    it.copy(
                        error = result.message.message,
                        isLoading = false
                    )
                }

                Resource.Loading -> updateState { it.copy(isLoading = true) }
            }
        }
    }
}