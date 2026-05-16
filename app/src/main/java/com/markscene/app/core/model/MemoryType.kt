package com.markscene.app.core.model

enum class MemoryType(val koreanName: String) {
    Idea("아이디어"),
    Work("업무"),
    Family("가족"),
    Childcare("육아"),
    Receipt("영수증"),
    Place("장소"),
    ItemLocation("물건 위치"),
    Document("문서"),
    Shopping("쇼핑"),
    Home("집"),
    SideProject("사이드 프로젝트"),
    Later("나중에 보기"),
    Emotion("감정 기록"),
    Other("기타");

    companion object {
        fun fromString(value: String): MemoryType? =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}
