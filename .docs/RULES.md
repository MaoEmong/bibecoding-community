# RULES.md (AI Vibe Coding Blueprint)

이 문서는 새로운 프로젝트를 시작할 때 재사용할 수 있는 "표준 뼈대" 규칙 문서다.  
기준 철학은 다음과 같다.

1. 빠르게 구현하되 구조를 망가뜨리지 않는다.
2. 기능 추가 속도보다 일관성과 회귀 방지를 우선한다.
3. AI Agent가 사람 없이도 안전하게 반복 작업할 수 있어야 한다.

---

## 1) 권장 기본 스택

프로젝트 성격(웹/API)에 따라 일부 교체 가능하지만 기본 추천 조합은 아래와 같다.

1. Language: Java 21
2. Framework: Spring Boot 4.x
3. View: Mustache
4. DB: H2(in-memory, 로컬 개발/테스트)
5. ORM: JPA (EntityManager 기반 구현)
6. Validation: Jakarta Validation
7. AOP: Spring AOP/AspectJ Starter
8. Test: JUnit 5 + Spring Test + Data JPA Test
9. Utility Library: Lombok, Jsoup(필요 시)

규칙:

1. 스택은 초기 합의 후 빈번히 바꾸지 않는다.
2. 새 라이브러리 추가 시 "왜 필요한지"를 README와 RULES에 기록한다.
3. 중복 기능 라이브러리 동시 도입 금지(예: 비슷한 Validation 라이브러리 2개).

---

## 2) 런타임 설정 핵심

`application.properties`에 최소 아래 항목을 갖춘다.

1. 서버 포트
2. 인코딩
3. 뷰 엔진 노출 정책(Session/Request Attribute)
4. Datasource
5. SQL 초기화(data.sql)
6. SQL 로그 정책(show-sql/format)
7. 세션 추적 방식(Cookie)
8. 세션 타임아웃

규칙:

1. 민감 설정은 코드 저장소에 직접 노출하지 않는다.
2. 기본값 변경(포트/세션/DB)은 문서화 필수.

---

## 3) 패키지 구조 원칙

### 3.1 Feature-based package structure

전역 레이어 폴더 분리보다 기능 중심(Vertical Slice) 구조를 기본으로 한다.

예시:

```text
com.example.project
├─ featureA
│  ├─ FeatureAController
│  ├─ FeatureAService
│  ├─ FeatureARepository
│  ├─ FeatureARequest
│  ├─ FeatureAResponse
│  └─ FeatureAEntity
├─ featureB
│  ├─ ...
└─ _core
   ├─ errors
   ├─ filter
   ├─ aop
   ├─ config
   └─ util
```

### 3.2 구조 규칙

1. 기능 단위로 코드를 모은다.
2. 공통 관심사는 `_core`로 분리한다.
3. `_core`는 도메인 세부 구현에 과도하게 의존하지 않는다.
4. 파일 생성은 "기능 응집도" 기준으로 한다.

---

## 4) 레이어 책임

1. Controller
: 라우팅, 요청 바인딩, 인증 컨텍스트 확인, 응답(View/API) 반환

2. Service
: 트랜잭션, 권한 검증, 도메인 규칙, 상태 변경

3. Repository
: 영속성 접근, 조회/저장/삭제, 쿼리 최적화

4. DTO(Request/Response)
: 입력 검증/출력 모델 전용. Entity 직접 노출 금지

5. Entity
: 영속 상태와 연관관계 중심. 프레젠테이션 책임 금지

규칙:

1. Controller에서 비즈니스 로직 작성 금지.
2. Service에서 View 포맷 조립 금지.
3. Repository에서 도메인 정책 판단 금지.

---

## 5) 의존 방향

기본 의존:

1. Controller -> Service
2. Service -> Repository
3. Service -> _core (필요 시)
4. Feature -> _core 허용
5. _core -> Feature 직접 의존 최소화

금지:

1. Controller -> Repository 직결
2. Feature A Service -> Feature B Repository 직접 접근
3. 순환 의존

---

## 6) 코드 스타일/구현 방식

### 6.1 코드 스타일

1. 클래스명: `도메인 + 역할` 패턴
2. 메서드명: 동작이 드러나는 이름
3. 매직 문자열/숫자 상수화
4. 주석은 "왜" 중심
5. 불필요한 추상화 금지

### 6.2 Repository 구현 방식 규칙

1. Repository는 `EntityManager` 기반으로 구현한다.
2. `JpaRepository` 중심 구현으로 혼합 전환하지 않는다.
3. 중간에 구현 방식 변경이 필요하면 사전 합의 후 문서 업데이트
4. Optional 반환 규칙을 통일한다.
5. 조회 실패 처리 책임은 Service가 담당한다.
6. Repository는 조회 결과를 `Optional`로 반환하고, 예외 변환은 하지 않는다.

### 6.3 네이밍 규칙

1. Request DTO: `XxxRequest` 또는 내부 클래스 `SaveDTO`, `UpdateDTO`
2. Response DTO: `XxxResponse` 또는 내부 클래스 `ListDTO`, `DetailDTO`
3. 테스트 메서드: "상황_행위_결과" 패턴

---

## 7) 요청 검증 규칙

1. 검증은 요청 경계(Controller 진입)에서 수행한다.
2. DTO 애노테이션 기반 검증을 기본으로 한다.
3. 공통 검증 로직은 AOP/Interceptor로 위임한다.
4. Controller마다 중복되는 검증 분기문을 최소화한다.

권장 제약 예시:

1. `@NotBlank`
2. `@Email`
3. `@Size`
4. `@Pattern`

---

## 8) 예외 처리 규칙

### 8.1 예외 전략

1. 도메인 의도가 드러나는 커스텀 예외를 사용한다.
2. 전역 예외 핸들러(`@ControllerAdvice`/`@RestControllerAdvice`)에서 일괄 처리한다.
3. 예외 메시지는 사용자 안내용과 내부 로그용을 구분한다.

### 8.2 HTTP 의미 매핑

1. 400: 잘못된 입력/검증 실패/중복
2. 401: 인증 필요 또는 인증 실패
3. 403: 권한 없음
4. 404: 리소스 없음
5. 409: 상태 충돌(필요 시)
6. 500: 서버 내부 오류

규칙:

1. 임의 상태코드 사용 금지.
2. 같은 상황에 서로 다른 코드 반환 금지.
3. 핸들러 응답 포맷(JSON/HTML)은 프로젝트 정책에 맞춰 단일화한다.

---

## 9) AOP 규칙

1. AOP는 횡단 관심사(검증, 로깅, 트랜잭션 경계 보조)에만 사용한다.
2. 핵심 도메인 로직을 AOP 내부에 과도하게 넣지 않는다.
3. 포인트컷 범위는 명시적이어야 하며, 문서에 대상 패턴을 기록한다.
4. AOP 도입 시 "적용 대상/미적용 대상"을 테스트로 보장한다.

---

## 10) 인증/권한/필터 규칙

### 10.1 인증 정책

1. 세션 또는 토큰 방식 중 하나를 초기 합의로 고정한다.
2. 인증 컨텍스트 키 이름은 전역 통일한다.
3. 로그아웃은 세션 무효화/토큰 폐기 정책을 명확히 한다.

### 10.2 권한 정책

1. 수정/삭제 권한은 소유자 또는 역할(Role) 기반으로 명시한다.
2. 권한 체크 위치는 Service를 기본으로 한다.
3. 권한 실패는 403으로 일관되게 처리한다.

### 10.3 필터/인터셉터 정책

1. URL 패턴은 "허용 목록" 또는 "차단 목록" 중 하나로 통일한다.
2. 인증 예외 URL(로그인, 정적 리소스 등)을 명시한다.
3. 필터 순서는 숫자(order)로 문서화한다.
4. 필터와 Controller 인증 로직이 중복되면 책임 경계를 정리한다.

---

## 11) 화면(View) 규칙

SSR 프로젝트 기준 규칙:

1. 공통 레이아웃/헤더/푸터 분리
2. 로그인 상태별 메뉴 분기 규칙 통일
3. 조건부 버튼 노출(소유자/권한 기반) 규칙 명시
4. 입력 폼의 검증 메시지 노출 방식 일관화
5. 리치 텍스트 에디터 사용 시 XSS/출력 정책 명시

---

## 12) 데이터/트랜잭션 규칙

1. 쓰기 작업은 Service 계층에서 트랜잭션 관리
2. 연관관계 변경 시 참조 정합성 먼저 보장
3. 삭제 시 참조 무결성/연쇄 영향 검토
4. Lazy/Eager 전략은 화면 요구사항과 성능을 같이 고려
5. N+1 발생 가능 지점은 쿼리 전략으로 보완

---

## 13) 테스트 규칙 (폴더 구조 포함)

### 13.1 테스트 계층 원칙

테스트는 실제 코드 구조와 최대한 유사한 계층으로 구성한다.

예시:

```text
src/main/java/com/example/project
└─ user/UserService.java

src/test/java/com/example/project
└─ user/UserServiceTest.java
```

### 13.2 폴더/파일 규칙

1. `src/test/java` 하위 경로는 `src/main/java`와 동일한 패키지 계층을 따른다.
2. 테스트 클래스명은 대상 클래스명 + `Test`를 기본으로 한다.
3. 통합 테스트는 suffix를 분리한다.
: 예) `UserControllerIntegrationTest`

### 13.3 테스트 시나리오 최소 기준

1. 정상 흐름 1개 이상
2. 검증 실패 1개 이상
3. 인증/권한 실패 1개 이상(해당 기능 시)
4. 예외 핸들러 응답 검증 1개 이상(해당 기능 시)

### 13.4 테스트 작성 규칙

1. 테스트명은 시나리오가 드러나게 작성
2. Given-When-Then 또는 Arrange-Act-Assert 패턴 유지
3. 실패 메시지는 원인 파악 가능하게 작성

---

## 14) 문서화 규칙

### 14.1 기본 문서

1. `README.md`: 실행/설정/구조/핵심 흐름
2. `RULES.md`: AI 및 개발 규칙
3. 필요 시 API/도메인 별 세부 문서

### 14.2 일일 작업 로그 규칙 (.context)

프로젝트 루트에 `.context` 폴더를 생성하고, 날짜 단위로 작업 로그를 기록한다.

폴더/파일 규칙:

1. 폴더: `.context/`
2. 파일명: `YYYY-MM-DD.md`
: 예) `.context/2026-02-09.md`

필수 기록 항목:

1. 오늘 목표
2. 수행 작업(파일 단위)
3. 결정 사항(왜 이렇게 했는지)
4. 테스트 결과
5. 남은 이슈/내일 할 일

권장 템플릿:

```md
# YYYY-MM-DD

## 목표
- ...

## 작업 내용
- 수정: path/to/file
- 추가: path/to/file

## 결정/근거
- ...

## 테스트
- 명령어:
- 결과:

## TODO
- ...
```

---

## 15) Git/협업 규칙

1. 커밋은 작고 의미 있게 분리
2. 커밋 메시지에 "무엇 + 왜" 포함
3. 임시 파일/개인 설정 파일 커밋 금지
4. PR 설명에 변경 범위/위험도/테스트 결과 포함

권장 타입:

1. `feat`
2. `fix`
3. `refactor`
4. `test`
5. `docs`
6. `chore`

---

## 16) AI Agent 실행 체크리스트

### 작업 전

1. 목표와 완료 조건을 정의했는가?
2. 영향 범위를 파일 단위로 식별했는가?
3. 기존 규칙과 충돌 여부를 확인했는가?

### 작업 중

1. 중간 결과를 작은 단위로 검증하는가?
2. 코드/테스트/문서를 함께 갱신하는가?
3. 보안/권한/검증 누락이 없는가?

### 작업 후

1. 빌드 성공
2. 테스트 통과
3. 변경 로그(.context) 기록
4. 리뷰 가능한 요약 작성

---

## 17) 금지 사항

1. 핵심 로직 변경 후 테스트 미작성
2. 예외/HTTP 코드 정책 무시
3. 인증/권한 검증 생략
4. 구조 원칙 무시한 무분별한 파일 추가
5. 비밀정보 하드코딩
6. 근거 없는 대규모 리팩터링
7. 코드 작성/수정/삭제를 사용자 확인 없이 진행

---

## 18) AI Prompt 템플릿

```text
[목표]
- (구현할 기능/수정할 버그)

[제약]
- Feature-based 구조 유지
- 검증/예외/권한 규칙 준수
- 기존 스타일/네이밍 유지

[필수 산출물]
- 코드 변경
- 테스트 추가/수정
- 문서 업데이트(README + .context 일지)

[검증]
- 빌드/테스트 명령 및 결과
- 주요 변경 파일 목록
- 리스크/후속 과제
```

---

## 19) 테스트 방식 보완 (MockMvc 미사용)

### 19.1 명칭
- MockMvc를 쓰지 않고 `@SpringBootTest` 환경에서 Controller 메서드를 직접 호출해 검증하는 방식은
  `컨트롤러 직접 호출 통합 테스트`로 명명한다.
- 영어 표기는 `Controller Direct Invocation Integration Test`를 사용한다.

### 19.2 적용 기준
- 화면 렌더링 자체보다 Controller의 분기/모델 주입/리다이렉트/flash 처리 검증이 목적일 때 우선 적용한다.
- 테스트에서 다음 객체를 직접 구성해 사용한다.
  - `Model` (`ExtendedModelMap`)
  - `BindingResult` (`BeanPropertyBindingResult`)
  - `HttpSession` (`MockHttpSession`)
  - `RedirectAttributes` (`RedirectAttributesModelMap`)

### 19.3 검증 항목
- `view name` 또는 `redirect url` 검증
- 모델 속성 주입값 검증
- validation 실패 시 flash attribute/리다이렉트 경로 검증
- 저장/수정 성공 후 Repository 조회로 실제 반영 여부 검증

---

## 20) 문서 포맷 보완 (현재 프로젝트 포맷)

### 20.1 .context 작업 로그 파일명
- 하루에 로그가 여러 번 생성되므로 아래 형식을 사용한다.
- 형식: `.context/YYYY-MM-DD-NN.md`
- 예시: `.context/2026-02-10-01.md`, `.context/2026-02-10-03.md`

### 20.2 .context 본문 포맷
- 아래 순서를 기본 포맷으로 사용한다.
1. `## 목표`
2. `## 작업 내용`
3. `## 결정/근거`
4. `## 테스트`
5. `## TODO`

### 20.3 .project 문서 파일명
- 작업 단위 문서는 번호 접두사를 사용한다.
- 형식: `.project/NN-제목.md`
- 예시: `.project/27-전역확인창로그아웃리다이렉트안정화.md`

### 20.4 문서 변경 원칙
- 기존 문서 본문을 광범위하게 재작성하지 않는다.
- 인접 이슈(테스트 방식/포맷/재발 방지)와 직접 연관된 항목만 수정 또는 추가한다.