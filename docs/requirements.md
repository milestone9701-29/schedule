# 요구사항 정리 (V2)

- [ ] 3-Layer, JPA, MySQL, Lombok, Auditing(수정 - 삭제)
- [ ] 회원가입, 로그인, 로그아웃, 내 정보(me) : 세션 인증
- [ ] 비밀번호 해시 저장(Spring Security : Crypto/Favre 중 택 1) : BCrypt
- [ ] Schedule CRUD — 소유자만 수정/삭제, 목록 페이징(기본 10, 상한 50)
- [ ] 목록에 commentCount 포함, 정렬 : updatedAt DESC(+ id DESC)
- [ ] Comment CRUD — 작성/수정/삭제(소유자), 스케줄 기준 조회 ASC
- [ ] 오류코드 표준화: 400/401/403/404/409/500
- [ ] ERD & 인덱스: `(user_id, updated_at)`, `(schedule_id, created_at)`
- [ ] README/Swagger/HTTP 스크립트/테스트(최소 2) :

## 메모(가설/결정)
- 인증 화이트리스트: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- 쿠키 속성: HttpOnly, SameSite=Lax, HTTPS 시 Secure
- 낙관적 잠금: @Version -> 충돌 시 409
- 어차피 해야할거라면 빠르게 진입할 필요성은 있을 듯.
