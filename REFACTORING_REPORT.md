# guardians-backend DDD + Facade 리팩토링 보고서

## 1. 개요

guardians-backend 프로젝트에 DDD(Domain-Driven Design) 3계층 구조, Facade 패턴, DIP(Dependency Inversion Principle)를 적용한 리팩토링이다.

**목적:** 컨트롤러-서비스-레포지토리 3계층에서 모든 비즈니스 로직이 `service/` 패키지에 몰려 있던 구조를 도메인 중심 설계로 전환한다.

**결과:** BUILD SUCCESSFUL. 에러 없음, 기존 Lombok `@Builder` 관련 경고만 존재(리팩토링과 무관).

**대상 도메인:** wargame, user, board, badge, job, auth, ranking (QnA 포함)

---

## 2. 리팩토링 전 문제점

### 2.1 서비스 레이어 과부하

모든 비즈니스 로직이 `com.guardians.service.{도메인}` 패키지의 ServiceImpl에 집중되어 있었다.

**WargameServiceImpl 예시:**
- 플래그 정답 판별(`wargameFlag.getFlag().equals(flag)`), 최초 풀이 판단, 점수 합산, 북마크/좋아요 토글 상태 결정, 관리자 권한 검증, Pod 이름 생성 규칙이 모두 단일 구현체 안에 인라인으로 존재
- Kubernetes 연산(`KubernetesPodServiceImpl`, `KubernetesKaliPodServiceImpl`)과 순수 도메인 로직이 동일 레이어에 혼재

**UserServiceImpl 예시:**
- `sessionUserId.equals(targetUserId)` 본인 확인 로직이 `updateUserInfo`, `changePassword`, `deleteUser` 세 메서드에 중복 존재
- `adminDeleteUser`에 `@Transactional` 누락

**BadgeServiceImpl 예시:**
- 뱃지 조건 판정 로직(totalSolved, HARD 3개, 카테고리별 3개, 7일 연속 등)이 서비스 구현체에 직접 구현

**BoardServiceImpl / CommentServiceImpl 예시:**
- 작성자 소유권 검증(`userId` 비교) 패턴이 두 구현체에 동일하게 반복
- `increaseViewCount`에서 `EntityNotFoundException`(JPA 표준)을 직접 사용하여 다른 메서드의 `CustomException` 패턴과 불일치

### 2.2 도메인이 인프라에 직접 의존

`domain/{도메인}/repository/` 하위의 JPA Repository 인터페이스가 도메인 패키지 안에 있어, 도메인 레이어가 Spring Data JPA(`JpaRepository`)에 직접 의존하고 있었다.

```
# 리팩토링 전 (문제)
com.guardians.domain.wargame.repository.WargameRepository       ← JpaRepository 상속
com.guardians.domain.user.repository.UserRepository             ← JpaRepository 상속
com.guardians.domain.badge.repository.BadgeRepository           ← JpaRepository 상속
```

서비스 구현체가 이 Repository를 직접 주입받아 사용했기 때문에, JPA 구현체를 교체하면 도메인 코드도 변경해야 하는 구조였다.

### 2.3 Kubernetes 인프라 로직의 서비스 노출

`KubernetesPodServiceImpl`과 `KubernetesKaliPodServiceImpl`이 `com.guardians.service.wargame` 패키지에 위치하여, fabric8 Kubernetes Client 의존이 서비스 레이어에 직접 노출되어 있었다.

### 2.4 도메인 간 순환 참조 위험

`BadgeServiceImpl`이 `WargameService`를 직접 주입받고, `WargameServiceImpl`이 `BadgeService`를 주입받는 구조로 도메인 간 의존 관계가 서비스 레이어에서 뒤엉켜 있었다.

---

## 3. 적용 패턴 및 설계 원칙

### 3.1 DDD (Domain-Driven Design)

- **Entity**: 비즈니스 개념을 표현하는 핵심 객체. 상태 변경 메서드(예: `wargame.increaseLikeCount()`, `user.updateLastLoginAt()`, `board.increaseViewCount()`)를 엔티티 안에 캡슐화한다.
- **Domain Service**: 단일 엔티티로 표현하기 어려운 도메인 규칙을 별도 컴포넌트로 분리한다. `WargameDomainService`(북마크/좋아요 토글, 플래그 정답 판별), `UserDomainService`(중복 검증, 본인 확인)가 이에 해당한다.

### 3.2 Facade 패턴

- **Application Facade**: 여러 도메인 Port와 Domain Service를 조합하여 하나의 유스케이스(Use Case)를 완성하는 조율자 역할이다.
- 컨트롤러는 오직 Facade만 호출한다. Facade는 세부 도메인 로직을 직접 구현하지 않고 Port와 DomainService에 위임한다.
- 트랜잭션 경계는 Facade에서 관리한다(`@Transactional(readOnly = true)` 기본, 쓰기 메서드 개별 선언).

### 3.3 DIP (Dependency Inversion Principle)

- **Port 인터페이스**: 도메인 레이어가 정의한 순수 Java 인터페이스. 인프라 기술에 대한 의존이 없다.
- **Adapter 구현체**: 인프라 레이어에서 Port를 구현한다. JpaRepository 상속, fabric8 Kubernetes Client 사용이 모두 이 레이어에 국한된다.
- 의존 방향: `Controller → Facade → Port ← Adapter → JpaRepository`

### 3.4 Hexagonal Architecture 요소

- **Incoming Port** 역할: Facade (컨트롤러에서 호출)
- **Outgoing Port** 역할: `domain/{도메인}/port/*.java` 인터페이스
- **Adapter (Secondary)**: `infrastructure/persistence/{도메인}/*Adapter.java`, `infrastructure/kubernetes/KubernetesPodAdapter.java`
- 외부 시스템(Kubernetes, S3, Redis, SMTP)은 모두 infrastructure 레이어 Adapter 뒤로 격리된다.

---

## 4. 변경된 패키지 구조

### Before

```
com.guardians
├── controller/
│   ├── WargameController.java
│   ├── UserController.java
│   ├── BoardController.java
│   └── ...
├── domain/
│   ├── badge/
│   │   ├── entity/
│   │   └── repository/          ← JpaRepository 상속 인터페이스가 도메인 안에 위치
│   ├── board/
│   │   ├── entity/
│   │   └── repository/
│   ├── job/
│   │   ├── entity/
│   │   └── repository/
│   ├── user/
│   │   ├── entity/
│   │   └── repository/
│   └── wargame/
│       ├── entity/
│       └── repository/
├── service/                      ← 모든 비즈니스 로직 집중
│   ├── answer/
│   ├── auth/
│   ├── badge/
│   ├── board/
│   ├── dashboard/
│   ├── job/
│   ├── mypage/
│   ├── question/
│   ├── s3/
│   ├── user/
│   │   └── impl/
│   └── wargame/                  ← Kubernetes 인프라 구현체가 서비스 레이어에 존재
├── dto/
└── ...
```

### After

```
com.guardians
├── controller/
│   ├── WargameController.java    ← Facade만 호출
│   ├── UserController.java
│   ├── BoardController.java
│   └── ...
├── domain/                       ← 순수 도메인 레이어 (JPA 의존 없음)
│   ├── badge/
│   │   ├── entity/
│   │   └── port/                 ← BadgePort, UserBadgePort (인터페이스)
│   ├── board/
│   │   ├── entity/
│   │   ├── port/                 ← AnswerPort, BoardLikePort, BoardPort, CommentPort, QuestionPort
│   │   └── repository/           ← CommentCountRepository (JPA 무관 프로젝션 인터페이스, 이동 권장)
│   ├── job/
│   │   ├── entity/
│   │   └── port/                 ← JobPort
│   ├── user/
│   │   ├── entity/
│   │   ├── port/                 ← UserPort, UserStatsPort
│   │   └── service/              ← UserDomainService (도메인 서비스)
│   └── wargame/
│       ├── entity/
│       ├── port/                 ← BookmarkPort, CategoryPort, KubernetesPodPort,
│       │                            ReviewPort, SolvedWargamePort, WargameFlagPort,
│       │                            WargameLikePort, WargamePort
│       └── service/              ← WargameDomainService (도메인 서비스)
├── application/                  ← 유스케이스 조율 레이어
│   ├── auth/
│   │   └── AuthFacade.java
│   ├── badge/
│   │   └── BadgeFacade.java
│   ├── board/
│   │   ├── BoardFacade.java
│   │   └── QnaFacade.java
│   ├── job/
│   │   └── JobFacade.java
│   ├── ranking/
│   │   └── RankingFacade.java
│   ├── user/
│   │   └── UserFacade.java
│   └── wargame/
│       └── WargameFacade.java
├── infrastructure/               ← 인프라 레이어 (JPA, Kubernetes 등)
│   ├── kubernetes/
│   │   └── KubernetesPodAdapter.java
│   └── persistence/
│       ├── badge/
│       │   ├── JpaBadgeRepository.java
│       │   ├── JpaUserBadgeRepository.java
│       │   ├── BadgeAdapter.java
│       │   └── UserBadgeAdapter.java
│       ├── board/
│       │   ├── JpaBoardRepository.java, JpaAnswerRepository.java, ...
│       │   ├── BoardAdapter.java, AnswerAdapter.java, ...
│       ├── job/
│       │   ├── JpaJobRepository.java
│       │   └── JobAdapter.java
│       ├── user/
│       │   ├── JpaUserRepository.java, JpaUserStatsRepository.java
│       │   ├── UserAdapter.java, UserStatsAdapter.java
│       └── wargame/
│           ├── JpaWargameRepository.java, JpaBookmarkRepository.java, ...
│           ├── WargameAdapter.java, BookmarkAdapter.java, ...
├── service/                      ← 유지된 인프라 유틸리티 서비스
│   ├── admin/HealthCheckService.java
│   ├── auth/EmailVerificationService.java
│   └── s3/S3Service.java, S3ServiceImpl.java
├── dto/
└── ...
```

---

## 5. 도메인별 변경 상세

### 5.1 wargame

**생성된 파일:**
- `domain/wargame/port/WargamePort.java`
- `domain/wargame/port/WargameFlagPort.java`
- `domain/wargame/port/SolvedWargamePort.java`
- `domain/wargame/port/BookmarkPort.java`
- `domain/wargame/port/WargameLikePort.java`
- `domain/wargame/port/ReviewPort.java`
- `domain/wargame/port/CategoryPort.java`
- `domain/wargame/port/KubernetesPodPort.java`
- `domain/wargame/service/WargameDomainService.java`
- `application/wargame/WargameFacade.java`
- `infrastructure/persistence/wargame/JpaWargameRepository.java` (외 6개 Jpa*Repository)
- `infrastructure/persistence/wargame/WargameAdapter.java` (외 6개 *Adapter)
- `infrastructure/kubernetes/KubernetesPodAdapter.java`

**수정된 파일:**
- `controller/WargameController.java` — `WargameService`, `KubernetesPodService`, `KubernetesKaliPodService` 주입 제거, `WargameFacade` 단일 주입으로 변경

**삭제된 파일:**
- `service/wargame/WargameService.java`
- `service/wargame/WargameServiceImpl.java`
- `service/wargame/KubernetesPodService.java`
- `service/wargame/KubernetesPodServiceImpl.java`
- `service/wargame/KubernetesKaliPodService.java`
- `service/wargame/KubernetesKaliPodServiceImpl.java`
- `domain/wargame/repository/` 하위 7개 Repository 파일

### 5.2 user

**생성된 파일:**
- `domain/user/port/UserPort.java`
- `domain/user/port/UserStatsPort.java`
- `domain/user/service/UserDomainService.java`
- `application/user/UserFacade.java`
- `infrastructure/persistence/user/JpaUserRepository.java`
- `infrastructure/persistence/user/JpaUserStatsRepository.java`
- `infrastructure/persistence/user/UserAdapter.java`
- `infrastructure/persistence/user/UserStatsAdapter.java`

**수정된 파일:**
- `controller/UserController.java` — `UserService` 제거, `UserFacade` 주입으로 변경

**삭제된 파일:**
- `service/user/UserService.java`
- `service/user/impl/UserServiceImpl.java`
- `domain/user/repository/UserRepository.java`
- `domain/user/repository/UserStatsRepository.java`

### 5.3 board

**생성된 파일:**
- `domain/board/port/BoardPort.java`
- `domain/board/port/BoardLikePort.java`
- `domain/board/port/CommentPort.java`
- `domain/board/port/AnswerPort.java`
- `domain/board/port/QuestionPort.java`
- `application/board/BoardFacade.java`
- `application/board/QnaFacade.java`
- `infrastructure/persistence/board/JpaBoardRepository.java` (외 4개)
- `infrastructure/persistence/board/BoardAdapter.java` (외 4개)

**수정된 파일:**
- `controller/BoardController.java` — `BoardService`, `CommentService` 제거, `BoardFacade` 단일 주입으로 변경
- `controller/QnaController.java` — `QuestionService`, `AnswerService`, `WargameRepository` 제거, `QnaFacade` 단일 주입으로 변경

**삭제된 파일:**
- `service/board/BoardService.java`, `BoardServiceImpl.java`
- `service/board/CommentService.java`, `CommentServiceImpl.java`
- `service/answer/AnswerService.java`, `AnswerServiceImpl.java`
- `service/question/QuestionService.java`, `QuestionServiceImpl.java`
- `domain/board/repository/` 하위 5개 Repository 파일 (CommentCountRepository 제외)

### 5.4 badge

**생성된 파일:**
- `domain/badge/port/BadgePort.java`
- `domain/badge/port/UserBadgePort.java`
- `application/badge/BadgeFacade.java`
- `infrastructure/persistence/badge/JpaBadgeRepository.java`
- `infrastructure/persistence/badge/JpaUserBadgeRepository.java`
- `infrastructure/persistence/badge/BadgeAdapter.java`
- `infrastructure/persistence/badge/UserBadgeAdapter.java`

**수정된 파일:**
- `controller/BadgeController.java` — `BadgeService` 제거, `BadgeFacade` 주입으로 변경

**삭제된 파일:**
- `service/badge/BadgeService.java`
- `service/badge/BadgeServiceImpl.java`
- `domain/badge/repository/BadgeRepository.java`
- `domain/badge/repository/UserBadgeRepository.java`

### 5.5 job

**생성된 파일:**
- `domain/job/port/JobPort.java`
- `application/job/JobFacade.java`
- `infrastructure/persistence/job/JpaJobRepository.java`
- `infrastructure/persistence/job/JobAdapter.java`

**수정된 파일:**
- `controller/JobController.java` — `JobService` 제거, `JobFacade` 주입으로 변경

**삭제된 파일:**
- `service/job/JobService.java`
- `service/job/JobServiceImpl.java`
- `domain/job/repository/JobRepository.java`

### 5.6 auth

**생성된 파일:**
- `application/auth/AuthFacade.java`

**유지된 파일:**
- `service/auth/EmailVerificationService.java` — Redis + SMTP 인프라 의존이 있는 구현체. 도메인 로직이 아닌 인프라 유틸리티로 판단하여 `service/auth/`에 유지하고, `AuthFacade`와 `UserFacade`가 직접 주입받아 사용한다.

**수정된 파일:**
- `controller/UserController.java` — 이메일 인증 관련 메서드를 `AuthFacade`를 통해 호출하도록 변경

### 5.7 ranking (mypage + dashboard 통합)

**생성된 파일:**
- `application/ranking/RankingFacade.java`

**수정된 파일:**
- `controller/RankingController.java` — `MypageService` 제거, `RankingFacade` 주입
- `controller/DashboardController.java` — `DashboardService` 제거, `RankingFacade` 주입
- `controller/MypageController.java` — `MypageService` 제거, `RankingFacade` 주입

**삭제된 파일:**
- `service/mypage/MypageService.java`, `MypageServiceImpl.java`
- `service/dashboard/DashboardService.java`, `DashboardServiceImpl.java`

---

## 6. 핵심 설계 결정

### 6.1 Port를 도메인 레이어에 배치한 이유

JPA Repository 인터페이스를 도메인 패키지 안에 두면 도메인이 Spring Data JPA에 직접 의존한다. 이를 끊기 위해 도메인이 필요한 데이터 접근 계약을 순수 Java 인터페이스(Port)로 정의하고, 구현은 infrastructure 레이어의 Adapter가 담당하도록 했다. 이로써 도메인 코드는 어떤 저장 기술이 사용되는지 알지 못한다.

### 6.2 Facade를 application 레이어에 배치한 이유

Facade는 트랜잭션 경계를 소유하고 여러 도메인 Port를 조합하는 역할이다. 단일 도메인 논리를 캡슐화하는 Domain Service와는 달리, Facade는 여러 도메인(예: WargameFacade가 UserPort, UserStatsPort를 함께 사용)에 걸친 흐름을 조율한다. 이 역할은 application 레이어에 적합하다.

### 6.3 DomainService의 책임 범위

`WargameDomainService`는 북마크/좋아요 토글(상태 결정 + 엔티티 likeCount 갱신)과 플래그 정답 판별을 담당한다. `UserDomainService`는 이메일·사용자명 중복 검증과 본인 확인(sessionUserId == targetUserId)을 담당한다. 두 DomainService 모두 Repository(Port)에 의존하여 판단에 필요한 데이터를 조회하며, 트랜잭션은 호출한 Facade에서 관리한다.

### 6.4 KubernetesPodPort를 domain/wargame/port에 배치한 이유

Kubernetes Pod 생성/삭제/상태 조회는 워게임 실행 환경을 구성하는 도메인 수준의 연산이다. 이를 `domain/wargame/port/KubernetesPodPort`로 정의하면, 도메인 코드는 "Pod를 시작하고 중지한다"는 계약만 알고, fabric8 Client와의 실제 통신은 `infrastructure/kubernetes/KubernetesPodAdapter`가 담당한다. Kubernetes를 다른 컨테이너 오케스트레이터로 교체할 때 Port 구현체(Adapter)만 교체하면 된다.

### 6.5 mypage, dashboard를 RankingFacade로 통합한 이유

기존 `MypageServiceImpl`과 `DashboardServiceImpl`은 `UserStatsPort`, `SolvedWargamePort`, `BookmarkPort`, `WargamePort` 등 동일한 Port를 반복 주입받아 유사한 조회 로직을 나누어 가지고 있었다. 두 서비스의 책임 범위가 "사용자 활동 통계 조회"로 수렴하므로 `RankingFacade`로 통합했다.

### 6.6 EmailVerificationService와 S3ServiceImpl을 유지한 이유

`EmailVerificationService`(Redis + SMTP)와 `S3ServiceImpl`(AWS S3)은 인프라 기술에 직접 의존하는 유틸리티 서비스다. 비즈니스 도메인 로직을 포함하지 않으므로 DDD Port/Adapter 패턴 적용 대상이 아니다. 두 파일은 `service/auth/`와 `service/s3/`에 유지한다. `S3ServiceImpl`은 추후 `infrastructure/s3/`로 이동하면 패키지 일관성이 개선된다.

---

## 7. 기대 효과

### 7.1 테스트 용이성

Port가 순수 Java 인터페이스이므로, Facade 단위 테스트에서 JPA 컨텍스트 없이 Port를 Mock으로 교체할 수 있다. 리팩토링 전에는 `WargameServiceImpl`을 테스트하려면 7개 Repository를 모두 Mock해야 했다. `WargameFacade`는 Port 인터페이스만 Mock하면 되며, `WargameDomainService`의 플래그 판별 로직은 Port 없이 순수 단위 테스트로 검증할 수 있다.

### 7.2 인프라 교체 유연성

JPA를 다른 저장 기술로 교체하거나, Kubernetes를 Docker Swarm으로 교체하는 경우 Adapter 파일만 교체하면 된다. Port 인터페이스와 Facade 코드는 변경이 없다.

### 7.3 코드 가독성과 유지보수성

컨트롤러는 어떤 서비스 구현체가 있는지 알지 못하고 Facade 메서드만 호출한다. 비즈니스 규칙의 위치가 명확하다: 단일 엔티티 상태 변경은 Entity 메서드, 다중 엔티티 간 규칙은 DomainService, 유스케이스 조합은 Facade. 신규 개발자가 플래그 판별 로직을 찾아야 할 때 `WargameDomainService.isCorrectFlag()`로 바로 찾을 수 있다.

### 7.4 SRP/DIP 준수

- **SRP**: WargameFacade는 유스케이스 조율, WargameDomainService는 도메인 규칙 판정, WargameAdapter는 JPA 저장 연산으로 책임이 분리된다.
- **DIP**: 도메인 레이어(Port)가 인프라 레이어(Adapter)를 의존하지 않는다. 의존 방향이 외부에서 내부(도메인)로 향한다.

---

## 8. 마이그레이션 시 주의사항

### 8.1 잔존 항목 및 후속 작업

| 항목 | 현황 | 권장 조치 |
|------|------|-----------|
| `service/s3/S3ServiceImpl.java` | DDD 3계층 구조 밖에 잔존 | `infrastructure/s3/`로 이동 |
| `domain/board/repository/CommentCountRepository.java` | `port/` 디렉토리가 아닌 `repository/`에 위치 | `domain/board/port/`로 이동하거나 `CommentPort`에 흡수 |
| `domain/wargame/port/WargamePort.java` | `Page<ResHotWargameDto>` 반환 타입에 `spring-data-commons` 의존 | 순수 DIP 관점에서 개선 여지 있음. 현재는 JPA 구현체 의존이 아니므로 허용 가능 |

### 8.2 Page/Pageable의 도메인 노출 판단

`WargamePort.findHotWargames(Pageable pageable)`와 `SolvedWargamePort`에 `org.springframework.data.domain.Page`, `org.springframework.data.domain.Pageable`이 사용된다. 이는 `spring-data-jpa`가 아닌 `spring-data-commons` 소속으로, JPA 구현체에 직접 의존하지 않는다. 다만 Spring 프레임워크 자체에 대한 도메인 의존이 생기므로, 팀 컨벤션에 따라 허용 여부를 결정한다. 완전한 프레임워크 독립이 필요하다면 커스텀 페이징 타입을 정의하거나 반환 타입을 `List`로 교체한다.

### 8.3 adminDeleteUser @Transactional 확인

기존 `UserServiceImpl.adminDeleteUser`에 `@Transactional` 누락이 있었다. `UserFacade.adminDeleteUser`에는 `@Transactional`이 명시적으로 선언되어 있어 이 문제가 수정되었다.

### 8.4 컨트롤러 세션 검증 패턴

컨트롤러의 `checkAdminWithDb` 헬퍼(JobController, WargameController 등)는 세션 userId로 DB에서 User를 재조회하여 `isAdmin()`을 검증하는 패턴이다. 이 패턴은 세션 role 값을 직접 신뢰하지 않는 보안 설계이므로 리팩토링에서 변경하지 않았다.

### 8.5 BadgeFacade의 경계

`BadgeFacade`는 뱃지 조건 판정 로직(`checkAndAssignBadges`)을 Facade 내부에 포함하고 있다. 뱃지 정책 변경이 빈번하다면 이 로직을 별도 `BadgePolicyEvaluator` DomainService로 추출하는 것을 검토한다.
