# API

## Auth
- POST `/api/auth/signup`
- POST `/api/auth/login`
## User
- GET `/api/users/{userId}` // 프로필
- GET `/api/users/me`

## Schedules
- POST `/api/schedules`
- GET  `/api/schedules/me`
- GET  `/api/schedules`
- PATCH `/api/schedules/{scheduleid}`
- DELETE `/api/schedules/{scheduleid}`

## Comments
- POST `/api/schedules/{scheduleId}/comments`
- GET  `/api/schedules/{scheduleId}/comments`
- PATCH `/api/schedules/{scheduleId}/comments/{commentId}`
- DELETE `/api/schedules/{scheduleId}/comments/{commentId}`


