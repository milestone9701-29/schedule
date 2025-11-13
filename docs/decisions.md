# 설계 결정 로그 (ADR-lite)

## 0001 — 인증 : 세션
- 대안: JWT
- 결과: 세션 재발급(changeSessionId), 화이트리스트 필터, 401/403 구분 명확.

## 0002 — 목록 : 페이징
- 이차 정렬 `updatedAt DESC`, `id DESC` : 동률 시 순서 보장.
- size 상한 50

## 0003 — 댓글 수 포함 방식
- Projection DTO + COUNT, 또는 서브쿼리. N+1 회피.

## 0004 — 동시성: @Version
- lost update 방지. 충돌 시 409로 응답.
