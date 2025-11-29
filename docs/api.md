# API

## Auth
- POST `/api/auth/signup`
- POST `/api/auth/login`

## User
- GET `/api/users/{userId}` // 프로필
- GET `/api/users/me`

## Schedule
- POST `/api/schedules`
- GET  `/api/schedules/me`
- GET  `/api/schedules`
- PATCH `/api/schedules/{scheduleid}`
- DELETE `/api/schedules/{scheduleid}`

## Comment
- POST `/api/schedules/{scheduleId}/comments`
- GET  `/api/schedules/{scheduleId}/comments`
- PATCH `/api/schedules/{scheduleId}/comments/{commentId}`
- DELETE `/api/schedules/{scheduleId}/comments/{commentId}`

# DTO

## Auth
### AuthMapper
- `public User ofSignUp(String encodedPassword, SignUpRequest request){}`
- `public UserSummaryResponse toUserSummary(User user){}`
### AuthTokens
- `public record AuthTokens(String accessToken, String refreshToken) {}`
### LoginRequest
- `String email, String password`
### LoginResponse
- `String accessToken, String refreshToken, UserSummaryResponse userSummaryResponse`
### SignUpResponse
- `String accessToken, String refreshToken, UserSummaryResponse userSummaryResponse`

## User
### UserSummaryResponse
- `Long id, String username, String email, LocalDateTime createdAt`
### UserMapper
- `public UserSummaryResponse toUserSummaryResponse(User user){}`
## Schedule
### ScheduleCreateRequest
- `String title, String content`
### ScheduleUpdateRequest
- `String title, String content, Long version`
### ScheduleResponse
- `Long id`
- `Long ownerId`
- `String ownerName`
- `String title`
- `String content`
- `Long version`
- `LocalDateTime createdAt`
- `LocalDateTime updatedAt`
### ScheduleMapper
- `public ScheduleResponse toScheduleResponse(Schedule schedule){}`


## Comment
### CommentCreateRequest
- `String content`
### CommentUpdateRequest
- `String content`
### CommentResponse
- `Long id;`
- `Long authorId;`
- `String authorName;`
- `String content;`
- `Long version;`
- `LocalDateTime createdAt;`
- `LocalDateTime updatedAt;`
### CommentMapper
- ` public CommentResponse toCommentResponse(Comment comment){}`
- `public List<CommentResponse> toCommentResponseList(List<Comment> comments){}`

