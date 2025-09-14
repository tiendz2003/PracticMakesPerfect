package com.manutd.ronaldo.practicemakesperfect.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContentProviderCompat.requireContext
import com.manutd.ronaldo.practicemakesperfect.data.di.IODispatcher
import com.manutd.ronaldo.practicemakesperfect.domain.repository.CameraRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject
import kotlin.io.copyTo

class CameraRepositoryImpl @Inject constructor(
    @param:IODispatcher val dispatcher: CoroutineDispatcher,
    @param:ApplicationContext val context: Context
) : CameraRepository {
    override suspend fun saveImageToGallery(uri: Uri): Uri? {
        return withContext(dispatcher) {
            try {
                val inputStream: InputStream? =
                    context.contentResolver.openInputStream(uri)

                val contentValues = ContentValues().apply {
                    val name = "${System.currentTimeMillis()}.jpg"
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ronaldo")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val contentResolver = context.contentResolver
                val newImageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                if (inputStream != null && newImageUri != null) {
                    contentResolver.openOutputStream(newImageUri).use { outputStream ->
                        inputStream.copyTo(outputStream!!)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(newImageUri, contentValues, null, null)
                    }
                    newImageUri
                } else {
                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun loadImage() {

    }
}