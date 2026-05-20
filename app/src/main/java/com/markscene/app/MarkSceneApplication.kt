package com.markscene.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.markscene.app.ui.util.ImageLoaderConfig

/**
 * Custom Application class that provides an optimized Coil ImageLoader
 * with OOM-safe settings (15% memory cache, RGB_565, hardware bitmaps).
 *
 * 이 클래스는 Coil ImageLoaderFactory를 구현하여 모든 AsyncImage 호출이
 * 자동으로 최적화된 ImageLoader를 사용하도록 합니다.
 */
class MarkSceneApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoaderConfig.create(this)
    }
}
