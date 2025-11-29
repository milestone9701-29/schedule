package com.tr.schedule.dto.comment;


import lombok.Value;

import java.time.LocalDateTime;


// Comment 출력 값 : id, author, content, 최초 생성일 - 갱신 생성일.
@Value
public class CommentResponse {
    Long id;
    Long authorId;
    String authorName;
    String content;
    Long version;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
