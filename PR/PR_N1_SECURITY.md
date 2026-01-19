# 📌 PR 제목
`refactor: N+1 쿼리 성능 최적화 및 Spring Security 보안 설정 강화`

---

## ✨ 주요 변경사항

### 성능 최적화 (N+1 쿼리 해결)
- `BoardServiceImpl.getHotBoards()`: 전체 Board 로드 후 메모리 정렬 → DB에서 상위 10개만 조회
- `MypageServiceImpl.getUserStats()`: 전체 유저 순회하며 랭크 계산 → PostgreSQL Window Function 사용
- `BadgeServiceImpl.checkAndAssignBadges()`: 카테고리별 개별 쿼리 → 단일 집계 쿼리로 통합

### 보안 강화
- `SecurityConfig`: CSRF 보호 활성화 및 명시적 엔드포인트 권한 설정

### 버그 수정
- `BadgeServiceImpl`: `Collections.frequency(Set, element)` 로직 오류 수정

---

## 🔍 상세 설명

### 왜 이 변경이 필요한가?

**1. N+1 쿼리 문제**
- `getHotBoards()`: 모든 게시글을 메모리에 로드한 후 정렬 → 데이터 증가 시 OOM 위험
- `getUserStats()`: 전체 유저 통계를 조회 후 반복문으로 랭크 계산 → O(n) 복잡도
- `checkAndAssignBadges()`: 카테고리마다 개별 쿼리 실행 → 5개 카테고리 = 5번 쿼리

**2. 보안 취약점**
- CSRF 완전 비활성화 상태 → CSRF 공격에 취약
- `/api/*` 패턴이 단일 레벨만 매칭 → 의도치 않은 인증 우회 가능

**3. 로직 버그**
- `Collections.frequency(Set, element)`는 Set 특성상 항상 0 또는 1 반환 → 카테고리별 풀이 수 집계 불가

### 어떤 흐름으로 작동하는가?

**BoardRepository 개선:**
```java
@Query("SELECT b FROM Board b ORDER BY (b.likeCount * 2 + b.viewCount) DESC LIMIT 10")
List<Board> findTop10ByHotScore();
```

**UserStatsRepository 개선:**
```java
@Query(value = """
    SELECT ranked.user_rank FROM (
        SELECT us.user_id, RANK() OVER (ORDER BY us.score DESC) as user_rank
        FROM user_stats us
    ) ranked WHERE ranked.user_id = :userId
    """, nativeQuery = true)
Optional<Integer> findUserRankByUserId(@Param("userId") Long userId);
```

**SolvedWargameRepository 개선:**
```java
@Query("SELECT w.category.name, COUNT(sw) FROM SolvedWargame sw " +
        "JOIN sw.wargame w WHERE sw.user.id = :userId GROUP BY w.category.name")
List<Object[]> countSolvedByCategory(@Param("userId") Long userId);
```

**SecurityConfig 개선:**
- `CookieCsrfTokenRepository.withHttpOnlyFalse()`: SPA 환경에서 JS가 쿠키 접근 가능
- 인증 엔드포인트(`/login`, `/signup`, `/logout`)는 CSRF 예외 처리
- `/api/boards/**`, `/api/wargames/**` 등 조회성 API는 명시적 `permitAll()`
- `/api/admin/**`은 `authenticated()` 필수

### 어떤 문제를 해결했는가?

| 항목 | Before | After |
|------|--------|-------|
| Hot 게시글 조회 | 전체 로드 + 메모리 정렬 | DB에서 상위 10개만 조회 |
| 랭크 계산 | O(n) 순회 | O(1) Window Function |
| 카테고리 집계 | 5개 쿼리 | 1개 집계 쿼리 |
| CSRF 보호 | 비활성화 | 쿠키 기반 토큰 활성화 |
| 뱃지 카테고리 체크 | 버그 (항상 0/1) | Map 기반 정확한 카운트 |

---

## ✅ 확인 리스트
- [x] 기능 정상 동작 확인
- [x] 에러 핸들링 및 예외 처리 확인
- [x] API 응답 형식 일관성 확인
- [x] 불필요한 로그/주석 제거

---

## 🧪 테스트 결과
- [x] Gradle 빌드 성공 (`./gradlew build`)
- [ ] Postman/Swagger로 API 테스트
- [ ] 프론트에서 연동 확인
- [ ] 유닛/통합 테스트 포함

---

## 📎 관련 이슈
- N/A (리팩토링 작업)

---

## 💬 기타 공유사항

### 프론트엔드 주의사항
CSRF 활성화로 인해 프론트엔드에서 다음 작업 필요:
1. 첫 요청 시 `XSRF-TOKEN` 쿠키 수신
2. 이후 POST/PUT/DELETE 요청 시 `X-XSRF-TOKEN` 헤더에 토큰 포함

```javascript
// axios 예시
axios.defaults.xsrfCookieName = 'XSRF-TOKEN';
axios.defaults.xsrfHeaderName = 'X-XSRF-TOKEN';
```

### 후속 작업 예정
- [ ] 인증 로직 중복 제거 (AuthUtil 유틸 클래스 생성)
- [ ] DTO Validation 어노테이션 추가
- [ ] User 엔티티 분리 (God Object 해소)
- [ ] 캐싱 적용 (랭킹, Hot 게시글)
