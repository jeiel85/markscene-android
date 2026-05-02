package com.markscene.app.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.BitmapFactory as GFXBitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageOptimizer {

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

            val exif = ExifInterface(bytes.inputStream())
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            val opts = GFXBitmapFactory.Options()
            opts.inJustDecodeSize = true
            GFXBitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
            
            opts.inSampleSize = calculateInSampleSize(opts, maxWidth, maxHeight)
            opts.inJustDecodeSize = false
            
            var bitmap = GFXBitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts) ?: return@withContext null

            bitmap = rotateBitmap(bitmap, orientation)

            val recordsDir = File(context.filesDir, "records").apply { mkdirs() }
            val outputFile = File(recordsDir, targetFileName)
            
            FileOutputStream(outputFile).use { out ->
                @Suppress("DEPRECATION")
                val format = if (android.os.Build.VERSION.SDK_INT >= 30) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                }
                bitmap.compress(format, quality, out)
            }
            
            bitmap.recycle()
            outputFile
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(options: GFXBitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val h = options.outHeight
        val w = options.outWidth
        var inSampleSize = 1
        if (h > reqHeight || w > reqWidth) {
            val halfH = h / 2
            val halfW = w / 2
            while (halfH / inSampleSize >= reqHeight && halfW / inSampleSize >= reqWidth) {
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
