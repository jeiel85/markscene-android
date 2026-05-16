package com.markscene.app.ui.util

object SearchQueryParser {
    data class ParsedQuery(
        val textQuery: String,
        val dateFilter: DateFilter?,
        val memoryTypeFilter: String?
    )

    enum class DateFilter { Today, Yesterday, ThisWeek, LastWeek }

    private val dateKeywords = mapOf(
        "오늘" to DateFilter.Today,
        "어제" to DateFilter.Yesterday,
        "이번 주" to DateFilter.ThisWeek,
        "이번주" to DateFilter.ThisWeek,
        "지난주" to DateFilter.LastWeek,
        "지난 주" to DateFilter.LastWeek
    )

    private val memoryTypeKeywords = mapOf(
        "아이디어" to "Idea",
        "업무" to "Work",
        "가족" to "Family",
        "육아" to "Childcare",
        "영수증" to "Receipt",
        "장소" to "Place",
        "문서" to "Document",
        "쇼핑" to "Shopping",
        "집" to "Home",
        "나중에 보기" to "Later",
        "나중에" to "Later"
    )

    fun parse(query: String): ParsedQuery {
        var dateFilter: DateFilter? = null
        var memoryTypeFilter: String? = null
        var textQuery = query

        for ((keyword, filter) in dateKeywords) {
            if (query.contains(keyword)) {
                dateFilter = filter
                break
            }
        }

        for ((keyword, filter) in memoryTypeKeywords) {
            if (query.contains(keyword)) {
                memoryTypeFilter = filter
                break
            }
        }

        return ParsedQuery(textQuery, dateFilter, memoryTypeFilter)
    }
}
