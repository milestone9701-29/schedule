package com.tr.schedule.dto.comment;

import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.User;
import org.springframework.stereotype.Component;


@Component
public class CommentMapper{

    // 객체화
    public Comment toEntity(User author, CommentCreateRequest request){
        return Comment.builder()
            .author(author)
            .content(request.getContent())
            .build();
    }
    // 응답 보내기
    public CommentResponse toResponse(Comment comment){
        return new CommentResponse(
            comment.getId(),
            comment.getAuthor(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }
}
