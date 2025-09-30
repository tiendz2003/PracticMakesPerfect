package com.manutd.ronaldo.practicemakesperfect.presentation.ui.movies

import android.R.id.message
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manutd.ronaldo.practicemakesperfect.domain.model.ChannelType
import com.manutd.ronaldo.practicemakesperfect.domain.repository.MoviesRepository
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.home.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        fetchMovies()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                val movies = moviesRepository.getMovies(
                    onError = { message ->
                        Log.e("MoviesViewModel", "Error fetching movies: $message")
                        UiState.Error("Error fetching movies: $message")

                    }
                )
                _uiState.value =
                    UiState.Success(movies.groups.filter { it.type == ChannelType.SLIDER })
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }

    }
}