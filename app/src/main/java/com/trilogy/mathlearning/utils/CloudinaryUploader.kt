package com.trilogy.mathlearning.utils

import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object CloudinaryUploader {

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dlgiukuzl",
            "api_key" to "256968335817433",
            "api_secret" to "ob5-9SfV8LbAxowHSXd9Op_2KqE"
        )
    )

    suspend fun uploadBitmap(bitmap: Bitmap): UiState<String> = withContext(Dispatchers.IO) {
        try {
            val bytes = bitmapToUploadBytes(bitmap)
            val result = cloudinary.uploader().upload(
                bytes,
                ObjectUtils.asMap(
                    "upload_preset", "zendo_upload_images"
                )
            )
            val url = result["secure_url"] as? String
            if (url.isNullOrBlank()) UiState.Failure("No secure_url")
            else UiState.Success(url)
        } catch (e: Exception) {
            UiState.Failure(e.message ?: "Upload error")
        }
    }

    // Giảm kích thước (cạnh dài 1024px) + JPEG 90%
    private suspend fun bitmapToUploadBytes(src: Bitmap): ByteArray = withContext(Dispatchers.Default) {
        val maxSide = 1024
        val w = src.width
        val h = src.height
        val scale = maxOf(1f, maxOf(w, h) / maxSide.toFloat())
        val targetW = (w / scale).toInt().coerceAtLeast(1)
        val targetH = (h / scale).toInt().coerceAtLeast(1)
        val resized = if (w > maxSide || h > maxSide) src.scale(targetW, targetH) else src
        val bos = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 90, bos)
        if (resized !== src) resized.recycle()
        bos.toByteArray()
    }
}
