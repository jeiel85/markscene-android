package com.markscene.app.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageOptimizer {

    /**
     * Optimize an image from a URI: downsample, rotate per EXIF, and compress to WebP.
     * Returns the optimized file, or null on failure.
     *
     * OOM 방어: inJustDecodeBounds로 먼저 크기를 측정한 후 적절한 inSampleSize를 계산하여
     * 메모리에 로드되는 비트맵 크기를 제한합니다. 최종 출력은 maxWidth x maxHeight 이내로 조정됩니다.
     */
    suspend fun optimize(
        context: Context,
        inputUri: Uri,
        targetFileName: String,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 80
    ): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(inputUri) ?: return@withContext null
            val bytes = inputStream.readBytes()
            inputStream.close()

            // Step 1: Decode bounds only to get original dimensions (OOM-safe)
            val boundsOpts = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOpts)

            // Step 2: Calculate optimal inSampleSize based on target dimensions
            val sampleSize = calculateInSampleSize(
                boundsOpts.outWidth, boundsOpts.outHeight,
                maxWidth, maxHeight
            )

            // Step 3: Decode with calculated inSampleSize
            val decodeOpts = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                // Use RGB_565 to halve memory usage when quality permits
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            var bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
                ?: return@withContext null

            // Step 4: Read EXIF orientation and rotate if needed
            val exif = ExifInterface(bytes.inputStream())
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            bitmap = rotateBitmap(bitmap, orientation)

            // Step 5: Further scale down to exact max dimensions if still too large
            if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val scale = minOf(
                    maxWidth.toFloat() / bitmap.width,
                    maxHeight.toFloat() / bitmap.height
                )
                val scaledWidth = (bitmap.width * scale).toInt()
                val scaledHeight = (bitmap.height * scale).toInt()
                val scaled = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
                if (scaled != bitmap) bitmap.recycle()
                bitmap = scaled
            }

            // Step 6: Save as WebP with quality setting
            val recordsDir = File(context.filesDir, "records").apply { mkdirs() }
            val outputFile = File(recordsDir, targetFileName)

            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.WEBP, quality, out)
            }

            bitmap.recycle()
            outputFile
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(
        rawWidth: Int,
        rawHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        if (rawHeight > reqHeight || rawWidth > reqWidth) {
            val halfHeight = rawHeight / 2
            val halfWidth = rawWidth / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
            if (it != bitmap) bitmap.recycle()
        }
    }
}
