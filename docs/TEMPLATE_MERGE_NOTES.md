# Template Merge Notes

## 통합 기준

이 문서 세트는 사용자의 기존 AGENTS 템플릿과 MarkScene 프로젝트 문서를 병합한 결과입니다.

반영한 기존 템플릿의 핵심 기준:

- Automation First 원칙
- 한국어 기반 커뮤니케이션
- 작업 전 최신 소스 동기화
- 작은 작업 단위와 범위 통제
- 파괴적 Git 명령 금지
- 의존성 추가 시 보안/라이선스/비용/정책 검토
- GitHub Actions 중심 최종 검증
- `HISTORY.md`와 `CHANGELOG.md` 중심 이력관리
- 커밋 전 `git diff` 직접 확인
- 완료 후 작업 요약 형식 고정

MarkScene 전용으로 추가한 기준:

- 외부 AI API Key 없이도 앱 기본 경험과 로컬 고급 AI 분석 제공
- 로컬 우선 사진 기록과 태그 검색
- 로컬 VLM 기반 고급 AI 분석
- Android Photo Picker 우선
- 광범위한 미디어 권한 금지
- 사용자 전체 갤러리 스캔 금지
- AI/로컬 분석 결과는 수정 가능한 제안으로 표시
- 모델 다운로드 토큰과 민감 데이터 로그 금지

## 문서 운영 방식

- `AGENTS.md`: 모든 에이전트가 반드시 따르는 최상위 규칙
- `docs/PRD.md`: 제품 요구사항의 기준
- `docs/ARCHITECTURE.md`: 구현 구조의 기준
- `docs/PRIVACY_AND_SECURITY.md`: 개인정보와 보안 판단의 기준
- `.agent/tasks.md`: 현재 작업 목록
- `.agent/progress.md`: 실제 작업 진행 기록
- `.agent/decisions.md`: 되돌리기 어려운 제품/기술 판단 기록
- `HISTORY.md`: 작업 이력
- `CHANGELOG.md`: 사용자에게 공개 가능한 변경 요약
