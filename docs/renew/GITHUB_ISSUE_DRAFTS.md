# MarkScene Scene Memory GitHub Issue Drafts

이 문서는 GitHub Issue로 바로 옮길 수 있는 작업 단위 초안이다.

---

# Epic: Scene Memory Extension

## 목표

MarkScene를 기존 비주얼 메모 앱에서 장면 기반 개인 기억 저장소로 확장한다.

## 핵심 기능

- Scene Timeline
- Memory Tags
- Recall Box
- Daily Scene Recap
- Ask My Scenes

---

## Issue 1. Today Screen / Scene Timeline 추가

```markdown
## 목표
기존 Record List를 날짜별 Scene Timeline으로 개선한다.

## 작업
- Today Screen 추가
- PhotoRecord를 날짜별로 그룹화
- SceneCard 컴포넌트 구현
- Empty State 개선
- Capture / Import FAB 연결

## 완료 기준
- 오늘 기록이 시간순으로 표시된다.
- 날짜별 그룹 헤더가 표시된다.
- SceneCard 클릭 시 상세 화면으로 이동한다.

## 검증
- 앱 실행 후 기존 기록이 Today 화면에 표시되는지 확인
- 기록이 없는 경우 Empty State가 표시되는지 확인
- SceneCard 클릭 시 Record Detail로 이동하는지 확인
```

---

## Issue 2. Memory Context 데이터 모델 추가

```markdown
## 목표
장면 기록에 기억 유형, 감정, 상황, Recall 여부를 저장할 수 있게 한다.

## 작업
- MemoryContextEntity 추가
- RecordMemoryTypeCrossRef 추가
- DAO / Repository 추가
- Room Migration 작성
- 기본 테스트 추가

## 완료 기준
- 기존 PhotoRecord와 연결된다.
- 기록 삭제 시 관련 MemoryContext도 삭제된다.
- 복수 Memory Type 저장이 가능하다.

## 검증
- 신규 기록 저장 시 MemoryContext가 함께 저장되는지 확인
- PhotoRecord 삭제 시 연결 데이터가 삭제되는지 확인
- Migration 후 기존 앱 데이터가 유지되는지 확인
```

---

## Issue 3. Create/Edit 화면에 Memory Tags 추가

```markdown
## 목표
기록 저장 시 사용자가 Memory Type을 선택할 수 있게 한다.

## 작업
- MemoryType enum 추가
- MemoryTypeChip 컴포넌트 추가
- Create/Edit 화면에 칩 그룹 표시
- 선택 상태 ViewModel 반영
- 저장 로직 연결

## 완료 기준
- 사용자가 하나 이상의 Memory Type을 선택할 수 있다.
- 선택하지 않아도 저장 가능하다.
- 저장 후 Detail 화면에 표시된다.

## 검증
- Memory Type 선택 후 저장
- 복수 선택 후 저장
- 미선택 저장
- 수정 화면에서 기존 선택 상태 복원
```

---

## Issue 4. Recall Box 추가

```markdown
## 목표
다시 볼 가치가 있는 기록을 별도 화면에서 모아 보여준다.

## 작업
- Recall Screen 추가
- isWorthRecalling 토글 추가
- idea/later 타입 자동 포함
- 메모 키워드 기반 Recall 후보 추가
- Bottom Navigation에 Recall 탭 추가

## 완료 기준
- Recall 탭에서 대상 기록이 보인다.
- 사용자가 Recall 여부를 수정할 수 있다.
- 삭제된 기록은 Recall 목록에서도 사라진다.

## 검증
- isWorthRecalling=true인 기록 표시
- Memory Type이 idea/later인 기록 표시
- 메모에 “나중에”, “확인”, “TODO”가 포함된 기록 표시
- 기록 삭제 후 Recall 목록에서 제거
```

---

## Issue 5. Daily Recap v1 추가

```markdown
## 목표
하루 기록 요약을 로컬 규칙 기반으로 제공한다.

## 작업
- GenerateDailyRecapUseCase 추가
- DailyRecapCard 추가
- top tags 계산
- memory type count 계산
- recall count 계산
- Today 화면에 요약 카드 표시

## 완료 기준
- Today 화면에 오늘 요약 카드가 표시된다.
- 네트워크 없이 동작한다.
- 기록 변경 시 요약이 갱신된다.

## 검증
- 오늘 기록 수 계산 확인
- 태그 TOP 5 계산 확인
- Memory Type별 개수 계산 확인
- Recall count 계산 확인
```

---

## Issue 6. Search 확장: Memory Type / Recall 필터

```markdown
## 목표
기존 태그/제목/메모 검색에 Memory Type과 Recall 필터를 추가한다.

## 작업
- SearchScenesUseCase 확장
- Memory Type 필터 칩 추가
- Recall 필터 추가
- 날짜 키워드 간단 파서 추가
- 검색 결과 SceneCard 적용

## 완료 기준
- “아이디어”, “영수증”, “나중에 보기” 필터가 동작한다.
- 오늘/어제/지난주 키워드가 동작한다.
- 기존 태그/제목/메모 검색이 깨지지 않는다.

## 검증
- 태그 검색
- 메모 검색
- Memory Type 검색
- 날짜 키워드 검색
- 복합 검색
```

---

## Issue 7. BYOK AI Scene Memory 분석 Schema 추가

```markdown
## 목표
고급 AI 분석 결과에 Scene Memory 정보를 포함할 수 있게 한다.

## 작업
- Advanced Scene Memory JSON Schema 추가
- Mock Provider 결과 확장
- Parser 확장
- 분석 결과 UI에 Memory Type / Recall 제안 표시
- 사용자가 수정 후 저장 가능하게 처리

## 완료 기준
- API Key 없이 Mock Provider로 UI 검증 가능
- AI 분석 결과가 제안으로만 표시된다.
- 사용자 수정 후 저장 가능하다.
- AI 실패 시 로컬 기록이 유지된다.

## 검증
- Mock 분석 성공
- JSON parse failure 처리
- Missing API key 처리
- Network/provider failure 처리
```

---

## Issue 8. 문서 업데이트

```markdown
## 목표
Scene Memory 확장 방향을 공식 문서에 반영한다.

## 작업
- README.md 포지션 문구 보완
- docs/PRD.md에 Scene Memory 확장 항목 추가
- docs/UX_FLOW.md에 Today/Recall 흐름 추가
- docs/DATA_MODEL.md에 MemoryContext 추가
- docs/DESIGN_SYSTEM.md에 Calm Memory UI 방향 추가
- CHANGELOG.md 업데이트
- HISTORY.md 업데이트

## 완료 기준
- 새 기능 방향이 문서에 반영된다.
- 기존 MVP 원칙과 충돌하지 않는다.
- 후속 에이전트가 문서만 보고 작업 가능하다.
```
