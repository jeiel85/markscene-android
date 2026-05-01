package com.markscene.app.data.backup

import android.content.Context
import android.net.Uri
import com.markscene.app.core.model.PhotoRecord
import com.markscene.app.data.record.RoomRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(
    private val context: Context,
    private val repository: RoomRecordRepository
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    suspend fun exportBackup(outputUri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val records = repository.observeRecords().first()
            val backupData = json.encodeToString(records)
            
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                    // 1. Write Data JSON
                    zipOut.putNextEntry(ZipEntry("data.json"))
                    zipOut.write(backupData.toByteArray())
                    zipOut.closeEntry()

                    // 2. Write Images
                    records.forEach { record ->
                        try {
                            val uri = Uri.parse(record.imageUri)
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                val fileName = "images/${record.id}.jpg"
                                zipOut.putNextEntry(ZipEntry(fileName))
                                input.copyTo(zipOut)
                                zipOut.closeEntry()
                            }
                        } catch (e: Exception) {
                            // Skip missing images
                        }
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(inputUri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            var importedCount = 0
            val imagesDir = File(context.filesDir, "records").apply { mkdirs() }
            var recordsJson: String? = null
            
            context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry = zipIn.getNextEntry()
                    while (entry != null) {
                        when {
                            entry.name == "data.json" -> {
                                recordsJson = zipIn.bufferedReader().readText()
                            }
                            entry.name.startsWith("images/") -> {
                                val recordId = entry.name.substringAfter("images/").substringBefore(".jpg")
                                val targetFile = File(imagesDir, "$recordId.jpg")
                                targetFile.outputStream().use { out ->
                                    zipIn.copyTo(out)
                                }
                            }
                        }
                        zipIn.closeEntry()
                        entry = zipIn.getNextEntry()
                    }
                }
            }

            val records = recordsJson?.let { json.decodeFromString<List<PhotoRecord>>(it) } ?: emptyList()
            records.forEach { record ->
                val localImageFile = File(imagesDir, "${record.id}.jpg")
                if (localImageFile.exists()) {
                    val updatedRecord = record.copy(
                        imageUri = Uri.fromFile(localImageFile).toString()
                    )
                    repository.saveRecord(updatedRecord)
                    importedCount++
                }
            }
            
            Result.success(importedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
