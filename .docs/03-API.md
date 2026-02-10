# 03 API 및 화면 라우팅

## 인증
1. GET `/login-form`
- 로그인 화면

2. POST `/login`
- 로그인 처리
- 성공: `/employees` 리다이렉트

3. POST `/logout`
- 로그아웃 처리
- 성공: `/login-form` 리다이렉트

## 사원
1. GET `/employees`
- 검색 + 페이지 목록

2. GET `/employees/save-form`
- 등록 폼

3. POST `/employees/save`
- 등록 처리

4. GET `/employees/{id}`
- 상세

5. GET `/employees/{id}/update-form`
- 수정 폼

6. POST `/employees/{id}/update`
- 수정 처리

7. POST `/employees/{id}/delete`
- 삭제 처리

## 예외 응답
- SSR 정책: 공통 alert 뷰로 안내 후 뒤로가기/이동
