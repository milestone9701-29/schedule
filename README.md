# Schedule API — V2 (Session Auth)

> 3-Layer + JPA + MySQL + Session 인증.
> <br> 비밀번호 해시(BCrypt), 페이징 기본(10, 최대 50), 댓글 개수 포함 목록, 예외 처리 준비. </br>


## Roadmap (D1~D3)
- D1: domain(User/Schedule/Comment), 인증(signup/login/logout/me), 기본 CRUD
- D2: 목록 페이징(+commentCount), 예외(ProblemDetail), 인덱스 적용
- D3: 낙관적 잠금(@Version), sanitize(JSoup), 문서화(swagger-ui)

## Directory (계획)
```
docs/                # 요구/설계/결정 로그
http/requests.http   # IntelliJ HTTP Client 스크립트
sql/init.sql         # 로컬 DB 초기화 스크립트
```

## Run (내일 채우기)
- JDK 17, Gradle, MySQL 8
- `application.yml` 작성 후 `./gradlew bootRun`

## Decisions (요약)
- 세션 기반 인증(화이트리스트 필터), BCrypt 단일화
- 목록: updatedAt DESC, size ≤ 50(DoS 방지), 안정 페이징(id DESC 2차 정렬)
- 예외: 400/401/403/404/409/500 표준화

## 이걸 본 사람의 반응
- 와 난 큰일낫다 : 열심히 할게요.
