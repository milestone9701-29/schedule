package com.tr.schedule.dto.comment;

import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.User;
import lombok.Value;

import java.time.LocalDateTime;


// Comment 출력 값 : id, author, content, 최초 생성일 - 갱신 생성일.
@Value
public class CommentResponse {
    Long id;
    User author;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
