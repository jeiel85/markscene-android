# MarkScene Design Direction

> **Design theme: Calm Memory UI**

## 1. 디자인 목표

MarkScene는 기존의 빠르고 깔끔한 비주얼 메모 도구에서,
조금 더 **개인적이고 차분한 기억 저장소**로 확장된다.

앱은 다음 느낌을 줘야 한다.

| 키워드 | 설명 |
|---|---|
| Calm | 기록과 회고에 집중할 수 있는 차분한 화면 |
| Visual | 사진이 주인공이 되는 레이아웃 |
| Private | 개인정보 앱처럼 신뢰감 있는 톤 |
| Light-weight | 사용자가 오래 입력하지 않아도 되는 구조 |
| Warm | 단순 도구가 아니라 개인 기억 앱처럼 부드러운 느낌 |

---

## 2. 디자인 원칙

### 2.1 사진을 작게 숨기지 않는다

MarkScene의 핵심은 사진이다. 리스트에서도 썸네일은 충분히 크게 보여줘야 한다.

권장 크기:

```text
Timeline thumbnail: 72dp ~ 96dp
Detail image: full width
Recap card image preview: 48dp ~ 64dp
```

### 2.2 태그보다 “장면”을 먼저 보여준다

확장 후에는 물체 태그보다 장면의 제목, 메모, 시간, 기억 유형이 더 중요하다.

우선순위:

```text
시간 → 사진 → 제목/메모 → Memory Type → 태그
```

### 2.3 카드는 많아도 화면은 가볍게

기록 앱은 카드가 많아질 수 있다.  
따라서 카드마다 장식 요소를 많이 넣지 않는다.

권장:

```text
- 흰색/다크 표면 카드
- 12~20dp radius
- 약한 elevation
- 태그는 작은 AssistChip
- 색상은 Memory Type에만 제한적으로 사용
```

### 2.4 감정 UI는 과하지 않게

Mood는 선택 기능으로 두고, 작은 칩으로 표시한다.

```text
🙂 좋음
😐 보통
😫 피곤
💡 아이디어
```

### 2.5 AI는 마법처럼 보이지 않게

AI 결과는 항상 “제안”으로 표현한다.

좋은 표현:

```text
추천 태그
분석 제안
장면 요약 초안
다시 볼 만한 기록으로 보입니다
```

피해야 할 표현:

```text
정확한 분석
완벽한 요약
사용자의 감정을 감지했습니다
모든 물체를 찾았습니다
```

---

## 3. 컬러 방향

Material 3 Dynamic Color를 기본 지원한다.

직접 브랜드 컬러를 잡는다면 다음 방향을 추천한다.

### Light Theme

```text
Primary: #4F6F64
Secondary: #7A6F5A
Tertiary: #5E6C8A
Background: #FAF8F3
Surface: #FFFFFF
Error: #BA1A1A
```

### Dark Theme

```text
Primary: #B7D0C3
Secondary: #D0C4AA
Tertiary: #C5CCE0
Background: #111412
Surface: #1A1D1A
Error: #FFB4AB
```

### Memory Type Accent

| Type | Accent 방향 |
|---|---|
| idea | Amber |
| work | Blue |
| family | Green |
| receipt | Gray |
| place | Purple |
| later | Orange |
| emotion | Rose |

주의: 색상만으로 상태를 구분하지 않는다. 텍스트와 아이콘을 함께 사용한다.

---

## 4. Typography

권장 Material 3 스타일:

| 용도 | 스타일 |
|---|---|
| 화면 제목 | `titleLarge` |
| 날짜 헤더 | `titleMedium` |
| 카드 제목 | `titleSmall` 또는 `bodyLarge` |
| 메모 일부 | `bodyMedium` |
| 태그 | `labelMedium` |
| 보조 정보 | `bodySmall` |

---

## 5. 주요 컴포넌트

### 5.1 SceneCard

```kotlin
@Composable
fun SceneCard(
    imageUri: String,
    timeText: String,
    title: String?,
    memo: String?,
    tags: List<String>,
    memoryTypes: List<MemoryType>,
    isWorthRecalling: Boolean,
    onClick: () -> Unit
)
```

표시 요소:

```text
- 시간
- 썸네일
- 제목 또는 메모 fallback
- 상위 태그 3개
- Memory Type chip
- Recall badge
```

---

### 5.2 DailyRecapCard

```kotlin
@Composable
fun DailyRecapCard(
    recordCount: Int,
    topTags: List<String>,
    memoryTypeCounts: Map<MemoryType, Int>,
    recallCount: Int,
    onClick: () -> Unit
)
```

표시 요소:

```text
오늘 6개의 장면을 남겼습니다.
아이디어 2개 · 다시 볼 기록 1개
많이 나온 태그: 노트북, 책상, 커피
```

---

### 5.3 MemoryTypeChip

```kotlin
@Composable
fun MemoryTypeChip(
    type: MemoryType,
    selected: Boolean,
    onClick: () -> Unit
)
```

표시 예시:

```text
[💡 아이디어]
[💼 업무]
[🏠 집]
[🧾 영수증]
[⭐ 나중에 보기]
```

---

### 5.4 RecallBadge

```kotlin
@Composable
fun RecallBadge(
    visible: Boolean
)
```

표시 예시:

```text
다시 보기
```

---

## 6. 화면별 UI 방향

### 6.1 Today Screen

```text
상단:
- 오늘 날짜
- “오늘 남긴 장면” 문구
- 검색 아이콘

요약:
- DailyRecapCard

본문:
- 날짜별 Scene Timeline

하단:
- FAB: Capture / Import
```

### 6.2 Create/Edit Scene Screen

```text
상단:
- 이미지 미리보기

입력:
- 제목
- 메모
- Suggested Tags
- Memory Tags
- Mood / Energy
- Recall toggle

하단:
- 저장 버튼
```

### 6.3 Search Screen

```text
상단:
- 검색창

필터:
- 오늘
- 이번 주
- 아이디어
- 영수증
- 나중에 보기
- 가족
- 업무

본문:
- 검색 결과 SceneCard 목록
```

### 6.4 Recall Screen

```text
상단:
- 다시 볼 기록 수
- 아이디어 수
- 확인 필요 수

본문:
- 아이디어
- 나중에 보기
- 확인 필요
- 오래된 Recall
```

---

## 7. Copywriting

### 온보딩

```text
사진을 찍고, 장면을 기록하세요.
MarkScene는 사진 속 단서와 직접 남긴 메모를 바탕으로
나중에 다시 찾을 수 있는 기억을 만들어줍니다.

기본 기능은 기기 안에서 동작합니다.
고급 AI 분석은 원할 때만 직접 켤 수 있습니다.
```

### Empty State

```text
아직 남긴 장면이 없습니다.
사진 한 장으로 오늘의 첫 기억을 만들어보세요.
```

### Recall Empty State

```text
아직 다시 볼 기록이 없습니다.
아이디어나 나중에 확인할 장면을 저장하면 이곳에 모입니다.
```

### AI CTA

```text
이 장면을 더 자세히 분석할까요?
선택한 이미지를 사용자의 AI Provider로 보내 장면 요약과 추천 태그를 생성합니다.
```
