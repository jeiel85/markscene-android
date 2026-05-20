package com.markscene.app.ui.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 디바이스 저장공간 관리를 위한 유틸리티.
 *
 * 오래된 임시 파일과 캐시를 자동 정리하여 저장공간 부족 상황을 방지합니다.
 * 앱 시작 시 비동기로 실행되며, UI 스레드를 차단하지 않습니다.
 */
object StorageCleaner {

    /** 7일 이상 된 임시 파일은 정리 대상 */
    private val TEMP_MAX_AGE_MS = TimeUnit.DAYS.toMillis(7)

    /** 캐시 디렉토리가 이 크기를 초과하면 오래된 항목부터 정리 */
    private const val CACHE_MAX_SIZE_BYTES = 300L * 1024 * 1024 // 300 MB

    /**
     * 오래된 임시 파일과 초과 캐시를 정리합니다.
     * 앱 시작 시 호출하는 것이 권장됩니다.
     *
     * @return 정리된 파일 수
     */
    suspend fun cleanup(context: Context): Int = withContext(Dispatchers.IO) {
        var cleanedCount = 0

        try {
            // 1. 오래된 records 임시 파일 정리
            val recordsDir = File(context.filesDir, "records")
            cleanedCount += cleanOldFiles(recordsDir, TEMP_MAX_AGE_MS)

            // 2. Coil 이미지 캐시 디렉토리 용량 확인 및 초과분 정리
            val imageCacheDir = context.cacheDir.resolve("image_cache")
            cleanedCount += cleanCacheIfOverLimit(imageCacheDir, CACHE_MAX_SIZE_BYTES)

            // 3. 일반 cache 디렉토리 정리 (7일 이상)
            cleanedCount += cleanOldFiles(context.cacheDir, TEMP_MAX_AGE_MS)

            // 4. camera temp 디렉토리 정리
            val cameraTempDir = File(context.cacheDir, "camera_temp")
            cleanedCount += cleanOldFiles(cameraTempDir, TimeUnit.DAYS.toMillis(3))
        } catch (e: Exception) {
            // 저장공간 정리 실패는 치명적이지 않음 - 조용히 넘어감
        }

        cleanedCount
    }

    private fun cleanOldFiles(dir: File, maxAgeMs: Long): Int {
        if (!dir.exists() || !dir.isDirectory) return 0
        val cutoffTime = System.currentTimeMillis() - maxAgeMs
        var count = 0
        dir.listFiles()?.forEach { file ->
            if (file.isFile && file.lastModified() < cutoffTime) {
                if (file.delete()) count++
            }
        }
        return count
    }

    private fun cleanCacheIfOverLimit(dir: File, maxSizeBytes: Long): Int {
        if (!dir.exists() || !dir.isDirectory) return 0
        val totalSize = dir.walkTopDown()
            .filter { it.isFile }
            .sumOf { it.length() }
        if (totalSize <= maxSizeBytes) return 0

        // 오래된 파일부터 삭제
        var count = 0
        dir.listFiles()?.sortedBy { it.lastModified() }?.forEach { file ->
            if (file.isFile && file.delete()) count++
        }
        return count
    }
}
