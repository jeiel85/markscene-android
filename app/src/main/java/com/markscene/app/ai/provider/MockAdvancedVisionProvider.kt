package com.markscene.app.ai.provider

import com.markscene.app.core.model.PhotoRecord

data class MockAdvancedAnalysisResult(
    val sceneSummary: String,
    val suggestedTags: List<String>,
    val warnings: List<String>
)

class MockAdvancedVisionProvider {
    fun analyze(record: PhotoRecord): MockAdvancedAnalysisResult {
        val baseTags = record.tags.map { it.name }.take(3)
        return MockAdvancedAnalysisResult(
            sceneSummary = "이 장면은 ${baseTags.joinToString(", ").ifBlank { "일반 환경" }} 중심으로 보입니다.",
            suggestedTags = (baseTags + listOf("정리", "기록")).distinct(),
            warnings = listOf("AI 결과는 제안이며 수정이 필요할 수 있습니다.")
        )
    }
}
