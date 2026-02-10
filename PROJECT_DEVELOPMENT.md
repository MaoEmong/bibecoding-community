# PROJECT_DEVELOPMENT.md

## 1. 문서 통합 상태
- 기존 개발 문서 폴더: `.project`, `.context`
- 통합 보관 폴더: `.docs`
- `.docs`에는 위 두 폴더의 `.md` 문서를 복사해 단일 위치에서 조회 가능하도록 정리함

## 2. 프로젝트 개요
- 프로젝트명: `bibecoding01`
- 성격: 사내 커뮤니티 + 사용자(사원) 관리 MVP
- 방식: Spring Boot 기반 SSR(Mustache)

## 3. 기술 스택
- Java 21
- Spring Boot 4.0.2
- Spring MVC, Validation, JPA
- H2(in-memory)
- Mustache
- Jsoup(XSS 방어)
- JUnit 5 기반 테스트

## 4. 기능 연혁 요약 (.project 01~27 기반)
1. 01~04: 요구사항/설계/API 정리, 기본 인증/사원관리 골격, 공통 레이아웃, 테스트 기반 확립
2. 05~08: 부서/직급 마스터 분리, 권한(ADMIN/EMPLOYEE) 도입 및 관리자 전용 정책 반영
3. 09~12: 사내 커뮤니티 전환, 게시글 기능 도입, Summernote 적용, XSS 방어 추가
4. 13~18: 댓글 CRUD 확장(등록/수정/삭제/정렬/수정일 노출), 화면 한글화 개선
5. 19~24: 목록 화면 Mustache 키 충돌/접근 오류 재발 방지, 메뉴/문구 안정화
6. 25~27: 목록 키 에러 근본 대응, 삭제/수정 confirm, 로그아웃 공통화, 리다이렉트/검증 실패 UX 안정화

## 5. 현재 핵심 도메인
- 인증/세션: `auth`
- 사용자/사원: `user`, `employee`
- 게시글/댓글: `post`, `comment`
- 기준정보: `department`, `position`
- 공통: `_core` (config, errors)

## 6. 현재 라우트(핵심)
- 인증
  - `GET /login-form`
  - `POST /login`
  - `POST /logout`
- 홈
  - `GET /`
  - `GET /main`
- 사용자 관리(관리자)
  - `GET /admin/users`
  - `POST /admin/users/save`
- 사원
  - `GET /employees`
  - `GET /employees/{id}`
  - `GET /employees/{id}/update-form`
  - `POST /employees/{id}/update`
  - `POST /employees/{id}/delete`
  - `GET /employees/save-form` -> `redirect:/admin/users`
  - `POST /employees/save` -> `redirect:/admin/users`
- 게시글/댓글
  - 게시글: `/posts` 하위 CRUD
  - 댓글: `/posts/{postId}/comments` 하위 save/update/delete
- 기준정보
  - `GET /departments`, `POST /departments/save`
  - `GET /positions`, `POST /positions/save`

## 7. 운영/개발 정책 요약
- 권한: 관리자 기능은 Interceptor + Service에서 이중 검증
- XSS: 게시글/댓글 저장 시 Jsoup 정제
- 예외: `@ControllerAdvice` 기반 alert 화면 + 리다이렉트 처리
- 템플릿 키 정책: Mustache 점 표기 키 충돌 이력으로 단일 키 사용 원칙 유지

## 8. 테스트 전략
- 서비스 통합 테스트 중심
- 필요 시 `컨트롤러 직접 호출 통합 테스트` 사용
  - `Model`, `BindingResult`, `MockHttpSession`, `RedirectAttributes`를 직접 구성해 검증
- 기준 명령: `./gradlew.bat test`

## 9. 최근 안정화 이슈(반영 완료)
- `UserRole.USER` 오기 -> `UserRole.EMPLOYEE` 정정
- `data.sql` BOM으로 인한 H2 SQL 실패 이슈 제거(UTF-8 without BOM)
- 사용자 관리 화면의 `request.*` 점표기 의존 제거 및 단일 키 바인딩 정리
- 메뉴 중복 라벨(`사용자/사원 등록` vs `사용자 관리`) 통합

## 10. 다음 권장 작업
1. 브라우저 E2E 점검 체크리스트 문서화
2. 인코딩(BOM) 재유입 방지 가이드/에디터 설정 명시
3. 사용자 관리 실패 케이스(중복/권한) 테스트 추가 강화
## 11. 개발 흐름 정리 (.context 기준)

아래 흐름은 `.context` 문서(`2026-02-09-01.md` ~ `2026-02-10-03.md`)를 기준으로 정리한 개발 타임라인이다.

### Phase 1: MVP 기초 구축 (2026-02-09-01 ~ 2026-02-09-04)
- 요구사항/설계/API 문서화 및 기본 구조 확정
- 인증(로그인/로그아웃), 사원 관리 기본 CRUD 골격 구성
- 공통 레이아웃(header/footer), 예외 처리, 테스트 기반 정리
- 관련 로그: `.context/2026-02-09-01.md` ~ `.context/2026-02-09-04.md`

### Phase 2: 기준정보/권한 체계 확장 (2026-02-09-05 ~ 2026-02-09-07)
- 부서/직급 마스터 분리 및 사원 도메인 연동
- 사용자 역할(ADMIN/EMPLOYEE) 도입
- 로그인 사용자 모델/인터셉터 기반 권한 제어 반영
- 관련 로그: `.context/2026-02-09-05.md` ~ `.context/2026-02-09-07.md`

### Phase 3: 커뮤니티 전환 및 보안 강화 (2026-02-09-08 ~ 2026-02-09-10)
- 사내 커뮤니티 전환, 게시글 기능(목록/작성/상세/수정/삭제) 도입
- Summernote 에디터 적용
- Jsoup 기반 XSS 방어 적용
- 관련 로그: `.context/2026-02-09-08.md` ~ `.context/2026-02-09-10.md`

### Phase 4: 댓글 기능 심화 (2026-02-09-11 ~ 2026-02-09-16)
- 댓글 등록/조회/삭제(1차) -> 수정(2차) -> 수정일 노출
- 댓글 정렬 옵션 추가 및 화면 문구 한글화
- 게시글 삭제 시 댓글 정리 정책 반영
- 관련 로그: `.context/2026-02-09-11.md` ~ `.context/2026-02-09-16.md`

### Phase 5: 목록 안정화 및 재발 방지 (2026-02-09-17 ~ 2026-02-09-22)
- `/posts`, `/employees` 접근 시 Mustache 키 에러 대응
- 템플릿 키/모델 전달 방식 단순화
- 설정 보강 및 재발 방지 조치, 작업일지 복구
- 관련 로그: `.context/2026-02-09-17.md` ~ `.context/2026-02-09-22.md`

### Phase 6: UX/운영 안정화 (2026-02-10-01 ~ 2026-02-10-03)
- 목록 키 에러 근본 대응(단일 키 + 기본 모델값 전략)
- 삭제 확인창, 빈값 제출 리다이렉트 개선
- 공통 헤더 로그아웃, 화면 한글 복구, 사용자 관리 안정화
- 관련 로그: `.context/2026-02-10-01.md` ~ `.context/2026-02-10-03.md`

### 현재 흐름 요약
1. MVP 기반 완성
2. 권한/도메인 확장
3. 커뮤니티 기능 도입
4. 댓글 기능 고도화
5. 템플릿/목록 안정화
6. UX/운영 안정화

이 흐름은 신규 작업 계획 수립 시 "어떤 맥락에서 현재 구조가 형성됐는지"를 판단하는 기준 타임라인으로 사용한다.
## 12. Phase별 핵심 파일 매핑

### Phase 1 (MVP 기초 구축)
- `src/main/java/com/example/bibecoding01/auth/AuthController.java`
- `src/main/java/com/example/bibecoding01/auth/AuthService.java`
- `src/main/java/com/example/bibecoding01/employee/EmployeeController.java`
- `src/main/java/com/example/bibecoding01/employee/EmployeeService.java`
- `src/main/resources/templates/auth/login-form.mustache`
- `src/main/resources/templates/employee/list.mustache`

### Phase 2 (기준정보/권한 확장)
- `src/main/java/com/example/bibecoding01/department/DepartmentController.java`
- `src/main/java/com/example/bibecoding01/position/PositionController.java`
- `src/main/java/com/example/bibecoding01/_core/config/AdminCheckInterceptor.java`
- `src/main/java/com/example/bibecoding01/_core/config/LoginCheckInterceptor.java`
- `src/main/java/com/example/bibecoding01/_core/config/WebMvcConfig.java`

### Phase 3 (커뮤니티 전환/보안 강화)
- `src/main/java/com/example/bibecoding01/post/PostController.java`
- `src/main/java/com/example/bibecoding01/post/PostService.java`
- `src/main/resources/templates/post/save-form.mustache`
- `src/main/resources/templates/post/detail.mustache`
- `build.gradle` (jsoup 의존성)

### Phase 4 (댓글 기능 심화)
- `src/main/java/com/example/bibecoding01/comment/CommentController.java`
- `src/main/java/com/example/bibecoding01/comment/CommentService.java`
- `src/main/resources/templates/comment/update-form.mustache`
- `src/main/resources/templates/post/detail.mustache`

### Phase 5 (목록 안정화/재발 방지)
- `src/main/java/com/example/bibecoding01/_core/config/ViewDefaultModelAdvice.java`
- `src/main/java/com/example/bibecoding01/post/PostController.java`
- `src/main/java/com/example/bibecoding01/employee/EmployeeController.java`
- `src/main/resources/templates/post/list.mustache`
- `src/main/resources/templates/employee/list.mustache`
- `src/main/resources/application.properties`

### Phase 6 (UX/운영 안정화)
- `src/main/java/com/example/bibecoding01/user/UserAdminController.java`
- `src/main/java/com/example/bibecoding01/_core/errors/GlobalExceptionHandler.java`
- `src/main/resources/templates/layout/header.mustache`
- `src/main/resources/templates/user/admin-list.mustache`
- `src/main/resources/templates/post/update-form.mustache`
- `src/main/resources/templates/employee/detail.mustache`

### 공통 데이터/검증 축
- `src/main/resources/data.sql`
- `src/test/java/com/example/bibecoding01/employee/EmployeeServiceIntegrationTest.java`
- `src/test/java/com/example/bibecoding01/post/PostServiceIntegrationTest.java`
- `src/test/java/com/example/bibecoding01/comment/CommentServiceIntegrationTest.java`
- `src/test/java/com/example/bibecoding01/user/UserAdminControllerIntegrationTest.java`