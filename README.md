# Bibecoding01 Onboarding

사내 커뮤니티 + 사용자(사원) 관리 MVP 프로젝트입니다.

## 1. 빠른 시작
### 요구사항
- JDK 21
- Gradle Wrapper 사용 가능 환경

### 실행
```bash
./gradlew.bat bootRun
```

### 테스트
```bash
./gradlew.bat test
```

## 2. 기본 접속 정보
- 앱 주소: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - User: `sa`
  - Password: (빈값)

## 3. 기본 관리자 계정
- 이름: `홍길동`
- 이메일: `ssar@nate.com`
- 비밀번호: `1234`
- 권한: `ADMIN`

## 4. 핵심 기능
- 인증/세션 로그인, 로그아웃
- 사용자 관리(관리자): `/admin/users`
- 사원 목록/상세/수정/삭제: `/employees`
- 부서/직급 관리: `/departments`, `/positions`
- 게시글/댓글 작성/수정/삭제 및 정렬
- 게시글/댓글 입력 XSS 방어(Jsoup)

## 5. 권한 모델
- `ADMIN`: 사용자 관리, 부서/직급 관리, 사원 수정/삭제 등 관리자 기능 수행
- `EMPLOYEE`: 일반 사용자 권한

## 6. 프로젝트 구조
```text
src/main/java/com/example/bibecoding01
- auth
- user
- employee
- post
- comment
- department
- position
- _core (config, errors)
```

## 7. 문서 위치
- 통합 문서 보관 폴더: `.docs`
  - 기존 `.project`, `.context` 문서를 통합 조회 가능
- 통합 개발 문서: `PROJECT_DEVELOPMENT.md`
- 규칙 문서: `.project/RULES.md`

## 8. 개발 시 유의사항
- Mustache 템플릿 키는 단일 키 중심으로 사용(점 표기 키 충돌 이력)
- SQL/Java/문서 파일은 UTF-8 without BOM 유지
- 예외/권한/검증 로직은 기존 `_core` 정책과 일관되게 반영

## 9. 주요 라우트
- `GET /login-form`, `POST /login`, `POST /logout`
- `GET /main`
- `GET /admin/users`, `POST /admin/users/save`
- `GET /employees`, `GET /employees/{id}`
- `GET /posts`, `GET /posts/{id}`

## 10. 현재 상태
- 사용자/사원 등록은 `/admin/users`로 통합 운영
- 메뉴는 `사용자 관리` 단일 항목으로 정리됨
- 최근 기준 전체 테스트 통과 상태