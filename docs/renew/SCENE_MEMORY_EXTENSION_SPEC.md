# MarkScene Scene Memory Extension Spec

> **MarkScene — 내 하루의 장면을 검색 가능한 기억으로.**

## 1. 문서 목적

이 문서는 기존 MarkScene 앱을 **사진 기반 비주얼 메모 앱**에서
**장면 기반 개인 기억 저장소**로 확장하기 위한 제품·UX·데이터 모델·개발 계획 설계서이다.

기존 MarkScene의 핵심은 다음 흐름이다.

```text
사진 촬영 또는 선택
  ↓
즉시 이미지 미리보기
  ↓
로컬 태그 초안 생성
  ↓
태그 칩 수정/삭제/추가
  ↓
로컬 기록 저장
  ↓
태그, 제목, 메모로 검색
```

Scene Memory 확장은 이 흐름을 유지하면서 다음 가치를 추가한다.

```text
기록 → 검색 → 회고 → 재발견
```

---

## 2. 기존 제품 원칙 유지

이 확장 설계는 기존 MarkScene의 제품 원칙을 깨지 않는다.

| 원칙 | 유지 방향 |
|---|---|
| Local-first | 기본 기록, 태그, 검색, 회고는 네트워크 없이 동작 |
| API Key 불필요 | 첫 사용 경험에서 API Key 요구 금지 |
| BYOK AI | 고급 분석만 사용자 API Key 기반으로 선택 실행 |
| 사용자 주도 AI | 외부 AI 분석은 명시적 액션 이후에만 실행 |
| 수정 가능한 제안 | 태그, 분석, 회고, 감정 추정은 모두 사용자가 수정 가능 |
| 개인정보 보호 | 자동 전체 갤러리 스캔, 자동 업로드, 백그라운드 위치 추적 금지 |

---

## 3. 제품 포지션 변경

### 기존 포지션

> 사진을 검색 가능한 비주얼 메모로 바꾸는 앱

### 확장 포지션

> 내 하루의 장면들을 검색 가능한 기억으로 바꾸는 앱

### 핵심 컨셉

MarkScene는 단순히 “무엇이 찍혔는지”를 저장하는 앱이 아니라,
사용자가 사진을 찍은 순간의 **장면, 생각, 상황, 감정, 다시 볼 가치**를 함께 저장하는 개인 기억 도구로 확장한다.

```text
사진
  ↓
장면 인식
  ↓
태그 + 메모 + 상황 + 기억 유형
  ↓
날짜별 타임라인
  ↓
검색
  ↓
회고
  ↓
다시 볼 생각 회수
```

---

## 4. 기능 네이밍

| 기능 | 제안 이름 | 설명 |
|---|---|---|
| 날짜별 기록 | Scene Timeline | 하루의 장면을 시간순으로 표시 |
| 기억 유형 | Memory Tags | 아이디어, 업무, 가족, 영수증 등 의미 태그 |
| 하루 요약 | Daily Scene Recap | 오늘 기록한 장면 요약 |
| 생각 회수 | Recall Box | 다시 볼 가치가 있는 기록 모음 |
| 자동 묶음 | Scene Collections | 비슷한 장면/태그를 자동 그룹화 |
| 자연어 검색 | Ask My Scenes | 문장형 검색 |

---

## 5. 핵심 기능 설계

### 5.1 Scene Timeline

#### 목적

기존 Record List를 단순 사진 목록이 아니라 **하루의 흐름을 보여주는 장면 타임라인**으로 개선한다.

#### 화면 예시

```text
오늘의 장면

09:12
[사진 썸네일]
책상 · 노트북 · 커피
메모: 아침에 개발 아이디어 정리

13:20
[사진 썸네일]
음식 · 식당
메모: 점심 먹으면서 앱 이름 생각남

22:40
[사진 썸네일]
노트북 · 키보드 · 메모
메모: MarkScene 회고 기능 아이디어
```

#### 요구사항

- 날짜별 그룹 표시
- 시간순 정렬
- 썸네일, 제목, 메모 일부, 상위 태그 표시
- Memory Type 표시
- Recall 대상 여부 표시
- 빠른 검색 진입 제공
- 빈 상태에서는 첫 기록 유도

#### MVP 범위

- `createdAt` 기준 날짜 그룹
- 오늘/어제/이전 날짜 구분
- 기존 `PhotoRecord`, `PhotoTag` 기반 표시
- 별도 AI 없이 동작

---

### 5.2 Memory Tags

#### 목적

기존 자동 태그는 물체 중심이다. Memory Tags는 사용자가 사진을 왜 저장했는지 표현하는 **의미 기반 태그**이다.

#### 기본 Memory Type

```text
idea
work
family
childcare
receipt
place
item_location
document
shopping
home
side_project
later
emotion
other
```

#### 한국어 표시명

| 저장값 | 표시명 |
|---|---|
| idea | 아이디어 |
| work | 업무 |
| family | 가족 |
| childcare | 육아 |
| receipt | 영수증 |
| place | 장소 |
| item_location | 물건 위치 |
| document | 문서 |
| shopping | 쇼핑 |
| home | 집 |
| side_project | 사이드 프로젝트 |
| later | 나중에 보기 |
| emotion | 감정 기록 |
| other | 기타 |

#### UX

기록 저장 화면에서 태그 칩 아래에 Memory Type 칩을 제공한다.

```text
이 장면은 어떤 기억인가요?

[아이디어] [업무] [가족] [영수증]
[물건 위치] [나중에 보기] [기타]
```

#### 설계 원칙

- 자동 단정 금지
- 사용자가 직접 선택 가능
- AI 추천은 “추천”으로만 표시
- 복수 선택 허용
- 미선택 저장 허용

---

### 5.3 Daily Scene Recap

#### 목적

하루 동안 저장한 기록을 요약하여 사용자가 자신의 하루를 빠르게 되돌아볼 수 있게 한다.

#### MVP v1: 로컬 규칙 기반 요약

AI 없이 다음 정보를 계산한다.

```text
오늘 저장한 장면 수
가장 많이 나온 태그 TOP 5
Memory Type별 개수
Recall 대상 기록 수
가장 최근 기록
가장 많이 기록한 시간대
```

#### 예시 출력

```text
오늘의 장면 요약

오늘 6개의 장면을 남겼습니다.

많이 나온 태그:
- 노트북
- 책상
- 커피
- 메모

기억 유형:
- 아이디어 2개
- 업무 1개
- 가족 1개
- 나중에 보기 1개

다시 볼 만한 기록이 2개 있습니다.
```

#### MVP v2: BYOK AI 요약

Gemini API Key가 설정된 사용자에게만 선택 기능으로 제공한다.

```text
오늘 장면들을 요약해줘.
반복되는 주제와 나중에 다시 볼 만한 기록을 찾아줘.
확실하지 않은 내용은 단정하지 말고 제안처럼 표현해줘.
```

---

### 5.4 Recall Box

#### 목적

사용자가 나중에 다시 봐야 할 가능성이 높은 기록을 자동 또는 수동으로 모아준다.

#### 대상 기록

| 조건 | 예 |
|---|---|
| Memory Type이 `idea` | 앱 아이디어, 개발 아이디어 |
| Memory Type이 `later` | 나중에 보기 |
| 메모에 특정 키워드 포함 | 나중에, 확인, 만들기, 사야 함, 정리, TODO |
| 사용자가 직접 별표 표시 | 수동 Recall |
| AI가 추천 | BYOK 사용 시 선택적 추천 |

#### 화면 예시

```text
Recall Box

다시 볼 생각

3일 전
[사진]
“MarkScene 회고 기능 아이디어”
태그: 노트북, 메모, 사이드 프로젝트

어제
[사진]
“이 상자 안 물건 정리 필요”
태그: 상자, 정리, 나중에 보기
```

#### MVP 범위

- 사용자가 직접 `다시 보기` 토글 가능
- Memory Type이 `idea`, `later`인 기록 자동 포함
- 메모 키워드 기반 포함
- AI 추천은 후순위

---

### 5.5 Ask My Scenes

#### 목적

기존 태그/제목/메모 검색을 유지하면서, 사용자가 자연어에 가까운 표현으로 기록을 찾을 수 있게 한다.

#### MVP v1: 로컬 키워드 검색 강화

기존 검색 대상:

```text
tag
title
memo
```

확장 검색 대상:

```text
tag
title
memo
memoryType
mood
contextType
sceneSummary
detectedObject.name
```

#### 검색 예시

```text
지난주 앱 아이디어
아이랑 찍은 사진
영수증
책상 위 충전기
나중에 볼 것
회사 메모
```

#### MVP v2: 자연어 Query Parser

AI 없이 간단한 규칙 기반으로 시작한다.

| 입력 | 해석 |
|---|---|
| 오늘 | createdAt = today |
| 어제 | createdAt = yesterday |
| 지난주 | createdAt in last 7 days |
| 아이디어 | memoryType = idea OR memo contains 아이디어 |
| 영수증 | memoryType = receipt OR tag contains receipt |
| 나중에 | isWorthRecalling = true OR memo contains 나중에 |

---

## 6. 정보 구조

### 추천 Bottom Navigation

```text
1. Today
2. Search
3. Recall
4. Settings
```

### 화면 구조

```text
Today
  - 오늘의 장면 타임라인
  - 오늘 요약 카드
  - 빠른 Capture / Import

Search
  - 검색 입력
  - 필터 칩
  - 결과 목록

Recall
  - 다시 볼 기록
  - 아이디어
  - 나중에 보기
  - 확인 필요

Settings
  - Advanced AI
  - Privacy
  - Appearance
  - About
```

---

## 7. 화면별 설계

### 7.1 Today Screen

#### 목적

앱을 열었을 때 “내가 오늘 무엇을 기록했는지” 바로 보여준다.

#### 구성

```text
상단
- 오늘 날짜
- 짧은 상태 문구
- 검색 아이콘

요약 카드
- 오늘 기록 수
- 아이디어 수
- 다시 볼 기록 수

타임라인
- 시간
- 썸네일
- 제목/메모
- 상위 태그
- Memory Type

FAB
- Capture
- Import
```

#### Empty State

```text
아직 오늘 남긴 장면이 없습니다.
사진을 찍거나 가져와서 첫 번째 기억을 남겨보세요.
```

---

### 7.2 Create/Edit Scene Screen

#### 목적

사진 한 장을 의미 있는 장면 기록으로 저장한다.

#### 구성

```text
1. 이미지 미리보기
2. 제목
3. 메모
4. Suggested Tags
5. Memory Tags
6. Mood / Energy
7. Recall 토글
8. 저장 버튼
```

#### 필드

| 필드 | 필수 여부 | 설명 |
|---|---:|---|
| imageUri | 필수 | 사진 경로 |
| title | 선택 | 사용자가 입력 |
| memo | 선택 | 장면 설명 |
| tags | 선택 | 자동/수동 태그 |
| memoryType | 선택 | 기억 유형 |
| mood | 선택 | 감정 |
| energy | 선택 | 에너지 |
| isWorthRecalling | 선택 | 다시 볼 기록 여부 |

#### 저장 버튼 조건

- 이미지는 준비되어 있어야 한다.
- 제목/메모/태그가 없어도 저장 가능하다.
- 태그 분석 실패 시에도 저장 가능해야 한다.

---

### 7.3 Record Detail Screen

#### 목적

하나의 장면 기록을 자세히 보여주고 수정할 수 있게 한다.

#### 구성

```text
이미지
제목
메모
태그
Memory Tags
Mood / Energy
분석 요약
생성 일시
수정 버튼
삭제 버튼
```

#### 고급 AI CTA

```text
더 자세히 분석하기

선택한 이미지를 사용자의 AI Provider로 보내 장면 요약과 물체 목록을 생성합니다.
```

---

### 7.4 Search Screen

#### 목적

사용자가 기억을 빠르게 찾을 수 있게 한다.

#### 구성

```text
검색창
필터 칩
- 오늘
- 이번 주
- 아이디어
- 영수증
- 나중에 보기
- 가족
- 업무

결과 목록
- 썸네일
- 제목/메모
- 태그
- 날짜
```

#### 검색 우선순위

1. 제목 정확 매칭
2. 태그 매칭
3. Memory Type 매칭
4. 메모 매칭
5. Scene Summary 매칭
6. Detected Object 매칭

---

### 7.5 Recall Screen

#### 목적

사용자가 잊기 쉬운 생각과 나중에 볼 기록을 다시 꺼내준다.

#### 구성

```text
상단 카드
- 다시 볼 기록 수
- 아이디어 수
- 확인 필요 수

섹션
1. 아이디어
2. 나중에 보기
3. 확인 필요
4. 오래된 Recall
```

#### 정렬

기본 정렬은 최근순이다.  
추후에는 오래된 중요 기록을 위로 올리는 “재발견” 정렬을 추가할 수 있다.

---

## 8. 데이터 모델 설계

기존 `PhotoRecord`, `PhotoTag`, `AdvancedAnalysis`, `DetectedObject` 구조를 유지하고,
Scene Memory 확장은 별도 테이블을 추가하는 방식으로 진행한다.

### 8.1 신규 Entity: MemoryContext

```kotlin
@Entity(
    tableName = "memory_contexts",
    foreignKeys = [
        ForeignKey(
            entity = PhotoRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("recordId"),
        Index("primaryMemoryType"),
        Index("isWorthRecalling"),
        Index("createdAt")
    ]
)
data class MemoryContextEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val primaryMemoryType: String?,
    val mood: String?,
    val energy: Int?,
    val contextType: String?,
    val isWorthRecalling: Boolean,
    val recallReason: String?,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 8.2 신규 Entity: RecordMemoryTypeCrossRef

```kotlin
@Entity(
    tableName = "record_memory_types",
    primaryKeys = ["recordId", "memoryType"],
    indices = [
        Index("recordId"),
        Index("memoryType")
    ]
)
data class RecordMemoryTypeCrossRef(
    val recordId: String,
    val memoryType: String,
    val source: String,
    val userConfirmed: Boolean,
    val createdAt: Long
)
```

### 8.3 신규 Entity: DailyRecapCache

```kotlin
@Entity(
    tableName = "daily_recap_cache",
    indices = [
        Index("dateKey", unique = true)
    ]
)
data class DailyRecapCacheEntity(
    @PrimaryKey val id: String,
    val dateKey: String, // yyyy-MM-dd
    val recordCount: Int,
    val recallCount: Int,
    val topTagsJson: String,
    val memoryTypeCountsJson: String,
    val localSummary: String?,
    val aiSummary: String?,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 8.4 Enum 제안

```kotlin
enum class MemoryType {
    Idea,
    Work,
    Family,
    Childcare,
    Receipt,
    Place,
    ItemLocation,
    Document,
    Shopping,
    Home,
    SideProject,
    Later,
    Emotion,
    Other
}

enum class MoodType {
    Good,
    Normal,
    Tired,
    Stressed,
    Calm,
    Excited,
    Unknown
}

enum class ContextType {
    Home,
    Work,
    Outside,
    Family,
    SideProject,
    Shopping,
    Travel,
    Unknown
}

enum class MemorySource {
    User,
    LocalRule,
    AdvancedAi
}
```

---

## 9. 도메인 Use Case 설계

### 9.1 SaveSceneRecordUseCase

```text
input:
- imageUri
- title
- memo
- tags
- memoryTypes
- mood
- energy
- contextType
- isWorthRecalling

process:
1. PhotoRecord 저장
2. PhotoTag 저장
3. MemoryContext 저장
4. RecordMemoryTypeCrossRef 저장
5. DailyRecapCache invalidation
```

### 9.2 GetTodayScenesUseCase

```text
input:
- date

output:
- List<SceneTimelineItem>

sort:
- createdAt descending or ascending
```

### 9.3 GenerateDailyRecapUseCase

```text
input:
- date

process:
1. 해당 날짜 PhotoRecord 조회
2. 태그 빈도 계산
3. Memory Type 카운트 계산
4. Recall 대상 개수 계산
5. 로컬 요약 문장 생성
6. 필요 시 cache 저장
```

### 9.4 GetRecallItemsUseCase

```text
input:
- filter: all | idea | later | checkNeeded

process:
1. isWorthRecalling = true 조회
2. memoryType in Idea/Later 조회
3. memo keyword matching
4. 최신순 정렬
```

### 9.5 SearchScenesUseCase

```text
input:
- query
- filters

process:
1. query normalize
2. date keyword parse
3. memory type keyword parse
4. Room FTS or LIKE search
5. result ranking
```

---

## 10. AI 확장 설계

### 10.1 AI 사용 원칙

- AI는 필수가 아니다.
- 로컬 기능이 항상 먼저 동작해야 한다.
- AI는 “고급 분석” 또는 “요약 제안”에만 사용한다.
- AI 결과는 저장 전 수정 가능해야 한다.
- 민감한 이미지 전송 가능성을 명확히 알려야 한다.
- API Key는 사용자가 직접 입력한다.

### 10.2 Advanced Scene Memory Schema

```json
{
  "sceneSummary": "string",
  "suggestedTitle": "string",
  "memoryTypes": ["idea", "work", "family"],
  "moodSuggestion": "calm|tired|stressed|good|unknown",
  "contextType": "home|work|outside|side_project|unknown",
  "recallCandidate": true,
  "recallReason": "string",
  "suggestedTags": ["string"],
  "warnings": ["string"]
}
```

### 10.3 AI Prompt 초안

```text
Analyze this image as a private personal scene memory.

Return only valid JSON matching the requested schema.

Rules:
- Do not claim certainty when uncertain.
- Treat all outputs as suggestions.
- Do not infer sensitive personal traits.
- Do not infer emotion unless clearly supported by visible content or user memo.
- Prefer practical tags that help future search.
- If the image looks like a note, receipt, desk, object location, family moment, or idea capture, suggest an appropriate memory type.
- If the record seems useful to revisit later, set recallCandidate to true and explain briefly.
```

### 10.4 AI Recap Prompt 초안

```text
Summarize the user's selected scene records for one day.

Input contains:
- titles
- memos
- tags
- memory types
- timestamps
- optional scene summaries

Rules:
- Do not invent events.
- Use only provided records.
- Be concise.
- Highlight repeated themes.
- Identify records worth revisiting.
- Avoid sensitive psychological conclusions.
- Phrase uncertain observations cautiously.
```

---

## 11. 개인정보 및 보안

### 11.1 유지할 보안 원칙

- 기본 사용에서 네트워크 전송 없음
- 선택된 이미지만 처리
- 전체 갤러리 자동 스캔 금지
- 백그라운드 위치 추적 금지
- API Key는 암호화 저장
- AI 응답 원문은 기본 저장하지 않음
- 로그에 사진 바이트, API Key, 프롬프트, AI 응답, 개인정보 남기지 않음

### 11.2 추가 주의사항

#### 감정 데이터

Mood는 민감한 개인 정보처럼 취급한다.

- 자동 단정 금지
- 사용자 선택 우선
- AI 추천은 선택적으로 표시
- 삭제 가능해야 함

#### 회고 데이터

Daily Recap은 사용자의 생활 패턴을 드러낼 수 있다.

- 로컬 저장 기본
- AI 요약 시 명시적 실행 필요
- AI 요약 전 전송 데이터 요약 표시 권장

#### Recall Box

Recall Box는 사용자가 잊지 말아야 할 것을 모으는 기능이다.  
따라서 삭제, 제외, 숨김 처리가 쉬워야 한다.

---

## 12. 개발 단계 제안

### Phase 1: Scene Timeline

#### 목표

기존 기록 목록을 날짜별 타임라인으로 개선한다.

#### 작업

- Today Screen 추가
- 날짜별 Record grouping
- SceneCard 컴포넌트 작성
- Empty State 개선
- FAB 액션 정리

#### 완료 기준

- 오늘 기록이 시간순으로 표시된다.
- 어제/이전 기록이 날짜별로 묶인다.
- 검색/상세 화면으로 이동 가능하다.

---

### Phase 2: Memory Tags

#### 목표

기록에 의미 기반 Memory Type을 추가한다.

#### 작업

- `MemoryContextEntity` 추가
- `RecordMemoryTypeCrossRef` 추가
- Create/Edit 화면에 Memory Type 칩 추가
- Detail 화면에 Memory Type 표시
- 검색 필터에 Memory Type 추가

#### 완료 기준

- 기록 저장 시 Memory Type 선택 가능
- 복수 Memory Type 저장 가능
- Search에서 Memory Type 필터 작동

---

### Phase 3: Recall Box

#### 목표

나중에 다시 볼 기록을 모아준다.

#### 작업

- `isWorthRecalling` 필드 저장
- Create/Edit 화면에 Recall 토글 추가
- Recall Screen 추가
- 로컬 키워드 기반 Recall 후보 표시
- `idea`, `later` 타입 자동 포함

#### 완료 기준

- Recall 탭에서 다시 볼 기록 확인 가능
- 기록 상세에서 Recall 여부 수정 가능
- 삭제 시 Recall 목록에서도 제거

---

### Phase 4: Daily Recap

#### 목표

하루 기록을 로컬 규칙으로 요약한다.

#### 작업

- `GenerateDailyRecapUseCase`
- `DailyRecapCard`
- 태그 빈도 계산
- Memory Type 카운트 계산
- Recall count 표시
- `DailyRecapCacheEntity` 선택 적용

#### 완료 기준

- Today 화면에서 오늘 요약 카드 표시
- 네트워크 없이 요약 동작
- 기록 추가/수정/삭제 시 요약 갱신

---

### Phase 5: BYOK AI Scene Memory

#### 목표

사용자 API Key 기반 고급 장면 기억 분석을 제공한다.

#### 작업

- Advanced Scene Memory Schema 추가
- Mock Provider 먼저 구현
- Gemini Provider 확장
- 외부 분석 경고 표시
- AI 제안을 Memory Type / Recall / Summary에 반영
- 사용자 수정 후 저장

#### 완료 기준

- API Key 없이 앱 기본 기능 사용 가능
- API Key 설정 시 고급 분석 가능
- AI 분석 실패해도 로컬 기록 유지
- AI 결과는 저장 전 수정 가능

---

## 13. 최종 방향 요약

```text
기존:
사진을 검색 가능한 비주얼 메모로 바꾸는 앱

확장:
내 하루의 장면을 검색 가능한 기억으로 바꾸는 앱
```

핵심 확장 기능은 다음 4개이다.

```text
1. Scene Timeline
2. Memory Tags
3. Daily Scene Recap
4. Recall Box
```

이 네 가지를 먼저 구현하면 MarkScene는 단순 사진 정리 앱이 아니라,
사용자가 매일 직접 쓰고 싶어지는 **개인 기억 저장소**가 된다.
