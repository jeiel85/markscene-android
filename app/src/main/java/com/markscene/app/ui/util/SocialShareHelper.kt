package com.markscene.app.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.markscene.app.core.model.PhotoRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Social sharing utility with Polaroid-style templates
 */
object SocialShareHelper {

    /**
     * Share record with Polaroid-style template
     */
    suspend fun shareWithTemplate(
        context: Context,
        record: PhotoRecord,
        template: ShareTemplate = ShareTemplate.POLAROID
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val bitmap = createPolaroidTemplate(context, record) ?: return@withContext false
            
            // Save to cache
            val cacheFile = File(context.cacheDir, "share_${record.id}.jpg")
            FileOutputStream(cacheFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            bitmap.recycle()

            // Get content URI
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "MarkScene으로 기록한 시각적 메모 #MarkScene")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "공유하기")
            context.startActivity(chooser)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun createPolaroidTemplate(context: Context, record: PhotoRecord): Bitmap? {
        // Simplified implementation - creates a white background Polaroid-style frame
        val width = 1080
        val imageHeight = 1080
        val bottomPadding = 200
        val height = imageHeight + bottomPadding

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // White background
        canvas.drawColor(Color.WHITE)

        // Draw border
        val paint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(40f, 40f, width - 40f, imageHeight.toFloat(), paint)

        // Draw text area
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 48f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        val title = record.title ?: "제목 없음"
        canvas.drawText(title, width / 2f, imageHeight + 80f, textPaint)

        // Draw app watermark
        val watermarkPaint = Paint().apply {
            color = Color.GRAY
            textSize = 32f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("MarkScene", width / 2f, height - 40f, watermarkPaint)

        return bitmap
    }
}

enum class ShareTemplate {
    POLAROID,
    MINIMAL,
    DETAILED
}