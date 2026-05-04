package com.markscene.app.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Utility class for stripping EXIF metadata from images
 * Removes GPS location, camera info, and other sensitive metadata
 */
object ExifStripper {

    /**
     * Strip all EXIF metadata from an image and return the clean URI
     */
    suspend fun stripExif(context: Context, imageUri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            // Create a temporary file
            val tempFile = File.createTempFile("clean_", ".jpg", context.cacheDir)
            
            // Decode the bitmap without EXIF
            val bitmap = decodeBitmap(context, imageUri) ?: return@withContext null
            
            // Save bitmap without any EXIF data
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            
            // Recycle bitmap to free memory
            bitmap.recycle()
            
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Copy EXIF data but remove sensitive fields (GPS, device info)
     */
    suspend fun stripSensitiveExif(context: Context, sourceUri: Uri, destFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                val sourceExif = ExifInterface(input)
                val destExif = ExifInterface(destFile)
                
                // Copy only non-sensitive tags
                val safeTags = listOf(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.TAG_IMAGE_WIDTH,
                    ExifInterface.TAG_IMAGE_LENGTH,
                    ExifInterface.TAG_MAKE,
                    ExifInterface.TAG_MODEL,
                    ExifInterface.TAG_DATETIME,
                    ExifInterface.TAG_FLASH,
                    ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
                    ExifInterface.TAG_F_NUMBER,
                    ExifInterface.TAG_SHUTTER_SPEED_VALUE,
                    ExifInterface.TAG_WHITE_BALANCE,
                    ExifInterface.TAG_EXPOSURE_TIME
                )
                
                safeTags.forEach { tag ->
                    sourceExif.getAttribute(tag)?.let { value ->
                        destExif.setAttribute(tag, value)
                    }
                }
                
                destExif.saveAttributes()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}