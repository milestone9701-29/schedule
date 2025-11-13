# API 설계 (초안)

## Auth
- POST `/api/auth/signup`
- POST `/api/auth/login`
- POST `/api/auth/logout`
- GET  `/api/auth/me`

## Schedules
- POST `/api/schedules`
- GET  `/api/schedules?page=&size=&author=&q=`  // 기본 sort: updatedAt DESC, size≤50
- GET  `/api/schedules/{id}`                     // 상세(+comments?) : 이건 보고.
- PATCH `/api/schedules/{id}`                    // 소유자
- DELETE `/api/schedules/{id}`                   // 소유자

## Comments
- POST `/api/schedules/{id}/comments`
- GET  `/api/schedules/{id}/comments`
- PATCH `/api/comments/{id}`                     // 소유자
- DELETE `/api/comments/{id}`                    // 소유자

## 오류코드 표
- 400 validation, 401 인증X, 403 권한X, 404 없음, 409 충돌/제한, 500 안전망
- 학습 진행에 따라 더 추가할 가능성 농후
