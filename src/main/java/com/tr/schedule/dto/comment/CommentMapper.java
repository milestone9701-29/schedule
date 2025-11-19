package com.tr.schedule.dto.comment;

import com.tr.schedule.domain.Comment;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CommentMapper{
    // 응답 보내기
    public CommentResponse toCommentResponse(Comment comment){
        return new CommentResponse(
            comment.getId(),
            comment.getAuthor().getId(),
            comment.getAuthor().getUsername(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }
    // List
    public List<CommentResponse> toCommentResponseList(List<Comment> comments){
		return comments.stream().map(this::toCommentResponse).toList();
    }
}
