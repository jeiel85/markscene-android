package com.markscene.app.data.backup

import com.markscene.app.core.model.PhotoRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun toCsv(records: List<PhotoRecord>): String {
        val header = "ID,Title,Space,Memo,OCR Text,Tags,Created At,Updated At,Image URI\n"
        val rows = records.joinToString("\n") { record ->
            val tags = record.tags.joinToString(";") { it.name }
            listOf(
                record.id,
                record.title.orEmpty().escapeCsv(),
                record.space.orEmpty().escapeCsv(),
                record.memo.orEmpty().escapeCsv(),
                record.ocrText.orEmpty().escapeCsv(),
                tags.escapeCsv(),
                dateFormat.format(Date(record.createdAt)),
                dateFormat.format(Date(record.updatedAt)),
                record.imageUri
            ).joinToString(",")
        }
        return header + rows
    }

    fun toMarkdown(record: PhotoRecord): String {
        val sb = StringBuilder()
        sb.append("# ${record.title ?: "Untitled Record"}\n\n")
        sb.append("## Metadata\n")
        sb.append("- **Space**: ${record.space ?: "Unassigned"}\n")
        sb.append("- **Created**: ${dateFormat.format(Date(record.createdAt))}\n")
        if (record.tags.isNotEmpty()) {
            sb.append("- **Tags**: ${record.tags.joinToString(" ") { "#${it.name}" }}\n")
        }
        sb.append("\n")

        sb.append("## Image\n")
        sb.append("![Record Image](${record.imageUri})\n\n")

        if (!record.memo.isNullOrBlank()) {
            sb.append("## Memo\n")
            sb.append("${record.memo}\n\n")
        }

        if (!record.ocrText.isNullOrBlank()) {
            sb.append("## OCR Text (Detected)\n")
            sb.append("```text\n")
            sb.append(record.ocrText)
            sb.append("\n```\n")
        }

        return sb.toString()
    }

    fun toMarkdownList(records: List<PhotoRecord>): String {
        return records.joinToString("\n\n---\n\n") { toMarkdown(it) }
    }

    private fun String.escapeCsv(): String {
        return if (contains(",") || contains("\"") || contains("\n")) {
            "\"" + replace("\"", "\"\"") + "\""
        } else {
            this
        }
    }
}
