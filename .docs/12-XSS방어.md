# 12 XSS방어

## 목표
- Summernote 본문 HTML 저장 시 XSS 위험을 서버에서 차단

## 적용 방식
1. 라이브러리
- `org.jsoup:jsoup` 추가

2. 게시글 정제
- 위치: `post/PostService`
- 저장(save), 수정(update) 시 정제 수행
- 제목: `Safelist.none()`
- 본문: `Safelist.relaxed()` + `style/class` 허용

3. 검증
- 통합 테스트에서 `<script>` 태그 제거 확인

## 주의
- 허용 태그 정책은 서비스 요구사항에 따라 조정 가능
