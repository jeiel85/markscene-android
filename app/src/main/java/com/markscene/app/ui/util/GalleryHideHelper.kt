package com.markscene.app.ui.util

import android.content.Context
import android.net.Uri
import java.io.File

object GalleryHideHelper {
    fun ensureNoMediaForRecordsDir(context: Context): Boolean {
        val recordsDir = File(context.filesDir, "records").apply { mkdirs() }
        val noMedia = File(recordsDir, ".nomedia")
        return if (noMedia.exists()) true else noMedia.createNewFile()
    }

    fun ensureNoMediaForImageUri(imageUri: String): Boolean {
        return try {
            val uri = Uri.parse(imageUri)
            if (uri.scheme != "file") return false
            val file = File(requireNotNull(uri.path))
            val parent = file.parentFile ?: return false
            val noMedia = File(parent, ".nomedia")
            if (noMedia.exists()) true else noMedia.createNewFile()
        } catch (_: Exception) {
            false
        }
    }
}
