package com.manutd.ronaldo.practicemakesperfect.domain.repository

import android.net.Uri

interface CameraRepository {
    suspend fun saveImageToGallery(uri: Uri): Uri?
    suspend fun loadImage()
}