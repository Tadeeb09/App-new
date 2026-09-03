package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.data.model.Attachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object FileHelper {

    suspend fun processUri(context: Context, uri: Uri): Attachment = withContext(Dispatchers.IO) {
        var fileName = "attachment"
        var sizeBytes = 0L
        val contentResolver = context.contentResolver

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex) ?: fileName
                if (sizeIndex >= 0) sizeBytes = cursor.getLong(sizeIndex)
            }
        }

        val mimeType = contentResolver.getType(uri) ?: when {
            fileName.endsWith(".png", true) -> "image/png"
            fileName.endsWith(".jpg", true) || fileName.endsWith(".jpeg", true) -> "image/jpeg"
            fileName.endsWith(".webp", true) -> "image/webp"
            fileName.endsWith(".pdf", true) -> "application/pdf"
            fileName.endsWith(".txt", true) -> "text/plain"
            fileName.endsWith(".csv", true) -> "text/csv"
            fileName.endsWith(".json", true) -> "application/json"
            else -> "application/octet-stream"
        }

        var base64Data: String? = null
        var textContent: String? = null

        val isImage = mimeType.startsWith("image/")

        if (isImage) {
            // Read image and scale down if necessary to avoid out-of-memory
            contentResolver.openInputStream(uri)?.use { stream ->
                val originalBitmap = BitmapFactory.decodeStream(stream)
                if (originalBitmap != null) {
                    val scaledBitmap = scaleBitmapIfNeeded(originalBitmap, 1536)
                    val outStream = ByteArrayOutputStream()
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
                    val bytes = outStream.toByteArray()
                    base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    if (sizeBytes == 0L) sizeBytes = bytes.size.toLong()
                }
            }
        } else if (mimeType.startsWith("text/") || mimeType == "application/json" || fileName.endsWith(".csv", true) || fileName.endsWith(".txt", true)) {
            // Read text content up to 100KB
            contentResolver.openInputStream(uri)?.use { stream ->
                val reader = stream.bufferedReader()
                textContent = reader.readText().take(50000)
            }
        }

        Attachment(
            uriString = uri.toString(),
            fileName = fileName,
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            base64Data = base64Data,
            textContent = textContent,
            isImage = isImage
        )
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (ratio > 1) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    suspend fun saveImageToMediaStore(context: Context, filePath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(filePath)
            if (!sourceFile.exists()) return@withContext Result.failure(Exception("File does not exist"))

            val fileName = "EVORO_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EVORO_AI")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext Result.failure(Exception("Failed to create MediaStore entry"))

            resolver.openOutputStream(imageUri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }

            Result.success("Saved to Gallery")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareImage(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) return

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share EVORO Image"))
        } catch (_: Exception) {}
    }
}
