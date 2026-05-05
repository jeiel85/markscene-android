package com.markscene.app.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageCropper {
    /**
     * Center-crop image to square and save as WEBP in cache.
     */
    fun cropCenterSquare(context: Context, inputUri: Uri): File? {
        val bitmap = context.contentResolver.openInputStream(inputUri)?.use { input ->
            BitmapFactory.decodeStream(input)
        } ?: return null

        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        val cropped = Bitmap.createBitmap(bitmap, x, y, size, size)

        val outFile = File(context.cacheDir, "crop_${UUID.randomUUID()}.webp")
        FileOutputStream(outFile).use { output ->
            cropped.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, output)
        }

        if (cropped != bitmap) bitmap.recycle()
        cropped.recycle()

        return outFile
    }
}
