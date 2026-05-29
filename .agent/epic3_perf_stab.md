> GitHub Issue: [#16](https://github.com/jeiel85/markscene-android/issues/16)

## Performance & Stability (성능 및 안정성) 전략

플레이 스토어 Top 10 진입을 위해 쾌적한 사용성과 앱 안정성을 확보하는 10가지 전략입니다.

- [x] **[Perf] 앱 시작 속도(Cold Start) 최적화**: Baseline Profile 적용 및 초기화 로직 지연 로딩을 통해 앱 실행 후 카메라가 켜지기까지의 시간을 1초 이내로 단축. (v2.4.0 - profileinstaller + baseline-prof.txt)
- [x] **[Perf] 이미지 로딩 및 캐싱 최적화**: Coil의 메모리/디스크 캐시 정책을 세밀하게 튜닝하여 수백 장의 사진 리스트를 스크롤해도 버벅임 없도록 개선. (v2.4.0 - MarkSceneApplication ImageLoaderFactory)
- [x] **[Stab] 예기치 않은 종료(Crash) 방어 강화**: OOM(Out of Memory) 방지를 위한 대용량 이미지 다운샘플링 처리 및 코루틴 예외 처리 철저. (v2.4.0 - ImageOptimizer OOM-safe 재작성)
- [ ] **[Perf] 배터리 소모 최소화**: 백그라운드 작업을 엄격히 통제하고, ML Kit의 이미지 분석을 코어 수에 맞게 조절하여 발열과 배터리 소모 억제.
- [x] **[Stab] 룸(Room) 데이터베이스 트랜잭션 최적화**: 수천 개의 태그와 레코드가 쌓여도 검색 쿼리 속도가 저하되지 않도록 적절한 인덱스(Index) 및 FTS(Full Text Search) 적용. (v2.4.0 - FTS4 인덱스)
- [ ] **[Perf] 카메라 센서 반응 속도 개선**: CameraX 설정 최적화를 통해 셔터랙(Shutter Lag)을 최소화하고 캡처 즉시 프리뷰를 렌더링하도록 개선.
- [ ] **[Stab] 모델 다운로드 단절/불안정 환경 대응 완벽화**: 대용량 로컬 모델 다운로드 중 네트워크가 끊겨도 안전하게 중단하고 재시도할 수 있는 로직 추가.
- [x] **[Perf] 리스트 스크롤 성능(Jank) 개선**: Jetpack Compose의 LazyColumn/LazyVerticalGrid 내에서 리컴포지션을 최소화하도록 키(key)와 람다 캐싱 철저 적용. (v2.4.0 - Strong Skipping + @Immutable)
- [x] **[Stab] 스토리지 용량 부족 사전 대응**: 디바이스 용량이 부족할 때 앱이 크래시되지 않고 오래된 임시 캐시를 자동 정리하며 사용자에게 알림 제공. (v2.4.0 - StorageCleaner)
- [x] **[Perf] APK/AAB 용량 다이어트**: 미사용 리소스 제거, ProGuard/R8 난독화 강화, 벡터 이미지 우선 사용으로 앱 다운로드 용량을 15MB 이하로 유지. (v2.4.0 - R8 minify + shrinkResources)
