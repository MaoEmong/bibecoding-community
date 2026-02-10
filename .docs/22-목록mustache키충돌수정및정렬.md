# 22-목록mustache키충돌수정및정렬

## 목표
- 게시판/사원목록 접근 시 발생한 Mustache 500 오류를 수정한다.
- 부서/직급 목록 정렬을 ID 오름차순으로 변경한다.

## 원인
- 템플릿에서 `request.keyword`, `request.employeeNo`를 사용했는데,
  `spring.mustache.servlet.expose-request-attributes=true` 설정으로 인해
  `request`가 모델 DTO가 아닌 `HttpServletRequest`로 해석되어 키 조회 실패.

## 조치
1. 모델 키 충돌 해소
- 목록 조건 DTO: `request` -> `condition`
- 입력 폼 DTO: `request` -> `form`
- 댓글 등록 폼 DTO: `commentRequest` -> `commentForm`

2. 템플릿 정리
- `post/list.mustache`, `employee/list.mustache`에서 `condition.*` 사용
- `post/update-form.mustache`, `employee/update-form.mustache`,
  `comment/update-form.mustache`에서 `form.*` 사용
- 깨진 텍스트/마크업 함께 정리

3. 정렬 수정
- `DepartmentRepositoryImpl.findAll()` -> `order by d.id asc`
- `PositionRepositoryImpl.findAll()` -> `order by p.id asc`

## 결과
- `/posts`, `/employees` 접근 시 Mustache 키 에러 제거
- 부서/직급 관리 목록이 ID 기준 오름차순 정렬
