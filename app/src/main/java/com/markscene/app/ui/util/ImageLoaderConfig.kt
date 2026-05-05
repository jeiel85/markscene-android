package com.markscene.app.ui.util

import android.content.Context
import android.graphics.Bitmap
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

/**
 * Optimized Coil ImageLoader for MarkScene
 * Configures memory and disk caching for optimal image loading performance
 * Includes OOM prevention measures: reduced memory footprint, RGB_565, hardware acceleration
 */
object ImageLoaderConfig {

    fun create(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.15) // Use 15% of available memory (reduced from 25% for OOM prevention)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(200 * 1024 * 1024) // 200 MB disk cache (reduced from 256 MB)
                    .build()
            }
            .bitmapConfig(Bitmap.Config.RGB_565) // Use RGB_565 to reduce memory per pixel (half of ARGB_8888)
            .allowHardware(true) // Enable hardware bitmaps (stored in native memory, not Java heap)
            .respectCacheHeaders(false) // Always use cached images
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .crossfade(200)
            .build()
    }
}