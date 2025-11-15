package com.tr.schedule.dto.comment;

import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CommentMapper{

    // 객체화
    public Comment toCommentEntity(User author, Schedule schedule, CommentCreateRequest request){
        return Comment.builder()
            .author(author)
            .content(request.getContent())
            .build();
    }
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
