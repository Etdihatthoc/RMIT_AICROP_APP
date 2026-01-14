package com.example.ai_crop_doctor.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper object for file operations
 */
object FileHelper {

    /**
     * Create a temporary image file for camera capture
     */
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "CROP_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    /**
     * Create a temporary audio file for voice recording
     */
    fun createAudioFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".3gp",
            storageDir
        )
    }

    /**
     * Copy file from Uri to app's internal storage
     */
    fun copyUriToFile(context: Context, uri: Uri, destinationFile: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            Timber.e(e, "Error copying file from URI")
            false
        }
    }

    /**
     * Save bitmap to file
     */
    fun saveBitmapToFile(bitmap: Bitmap, file: File, quality: Int = 90): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            true
        } catch (e: IOException) {
            Timber.e(e, "Error saving bitmap to file")
            false
        }
    }

    /**
     * Delete file safely
     */
    fun deleteFile(file: File): Boolean {
        return try {
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting file")
            false
        }
    }

    /**
     * Get file size in MB
     */
    fun getFileSizeMB(file: File): Double {
        return file.length() / (1024.0 * 1024.0)
    }

    /**
     * Check if file is an image
     */
    fun isImageFile(file: File): Boolean {
        val extension = file.extension.lowercase()
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }

    /**
     * Check if file is an audio file
     */
    fun isAudioFile(file: File): Boolean {
        val extension = file.extension.lowercase()
        return extension in listOf("mp3", "wav", "3gp", "m4a", "aac")
    }

    /**
     * Format file size for display
     */
    fun formatFileSize(sizeInBytes: Long): String {
        val kb = sizeInBytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1 -> String.format(Locale.US, "%.2f MB", mb)
            kb >= 1 -> String.format(Locale.US, "%.2f KB", kb)
            else -> "$sizeInBytes bytes"
        }
    }
}
