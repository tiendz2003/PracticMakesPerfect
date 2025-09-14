package com.manutd.ronaldo.practicemakesperfect.presentation.ui.account

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manutd.ronaldo.practicemakesperfect.domain.repository.CameraRepository
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    savedStateHandle: SavedStateHandle
)  : ViewModel() {
    private val _imageBitmap = savedStateHandle.get<String>("imageUri")
    val imageUri = _imageBitmap?.toUri()
    private val _imageSaveResult = MutableLiveData<Uri?>()
    val imageSaveResult: LiveData<Uri?> get() = _imageSaveResult
    fun saveImage() {
        viewModelScope.launch {
            try {
                imageUri?.let {
                    val savedUri = cameraRepository.saveImageToGallery(it)
                    _imageSaveResult.postValue(savedUri)
                }
            } catch (e: Exception) {
                _imageSaveResult.postValue(null)
            }
        }
    }
}