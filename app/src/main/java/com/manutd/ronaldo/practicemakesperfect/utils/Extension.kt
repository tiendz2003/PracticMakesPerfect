package com.manutd.ronaldo.practicemakesperfect.utils

import android.R.attr.bitmap
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import androidx.camera.core.ImageProxy
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toFormattedDate(
    outputPattern: String = "dd/MM/yyyy HH:mm",
    locale: Locale = Locale.getDefault()
): String {
    return try {
        val instant = Instant.parse(this) // parse "2019-03-07T19:00:00Z"
        val formatter = DateTimeFormatter.ofPattern(outputPattern, locale)
            .withZone(ZoneId.systemDefault()) // chuyển sang timezone local
        formatter.format(instant)
    } catch (e: Exception) {
        this // Nếu parse lỗi thì trả lại chuỗi gốc
    }
}

fun ImageProxy.imageProxyToBitmap(): Bitmap {
    val buffer = this.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val initialBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Xoay ảnh cho đúng chiều (nếu cần)
    val matrix = Matrix().apply {
        postRotate(this@imageProxyToBitmap.imageInfo.rotationDegrees.toFloat())
    }
    return Bitmap.createBitmap(
        initialBitmap,
        0,
        0,
        initialBitmap.width,
        initialBitmap.height,
        matrix,
        true
    )
}

// Hàm helper để lưu Bitmap vào thư mục cache và trả về Uri
fun Bitmap.saveBitmapToCache(context: Context): Uri? {
    return try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs() // Tạo thư mục nếu chưa có
        val file = File(cachePath, "image_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        // Lấy Uri thông qua FileProvider (best practice)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Đảm bảo provider này đã được khai báo trong AndroidManifest
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Int.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}