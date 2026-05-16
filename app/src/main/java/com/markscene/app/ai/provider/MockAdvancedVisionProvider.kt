package com.markscene.app.ai.provider

import com.markscene.app.core.model.PhotoRecord

data class MockAdvancedAnalysisResult(
    val sceneSummary: String,
    val suggestedTags: List<String>,
    val warnings: List<String>,
    val memoryTypes: List<String> = emptyList(),
    val recallCandidate: Boolean = false,
    val recallReason: String? = null,
    val moodSuggestion: String? = null,
    val contextType: String? = null
)

class MockAdvancedVisionProvider {
    fun analyze(record: PhotoRecord): MockAdvancedAnalysisResult {
        val baseTags = record.tags.map { it.name }.take(3)
        val hasMemo = !record.memo.isNullOrBlank()
        val memoLower = record.memo?.lowercase() ?: ""
        val recallKeywords = listOf("나중에", "확인", "만들기", "사야", "정리", "TODO", "idea", "아이디어")
        val isWorthRecall = recallKeywords.any { memoLower.contains(it.lowercase()) }

        return MockAdvancedAnalysisResult(
            sceneSummary = "이 장면은 ${baseTags.joinToString(", ").ifBlank { "일반 환경" }} 중심으로 보입니다.",
            suggestedTags = (baseTags + listOf("정리", "기록")).distinct(),
            warnings = listOf("AI 결과는 제안이며 수정이 필요할 수 있습니다."),
            memoryTypes = if (hasMemo && isWorthRecall) listOf("Idea") else emptyList(),
            recallCandidate = isWorthRecall,
            recallReason = if (isWorthRecall) "메모에서 다시 확인할 내용이 감지되었습니다." else null,
            moodSuggestion = null,
            contextType = null
        )
    }
}
