package com.tr.schedule.service;


import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.comment.CommentCreateRequest;
import com.tr.schedule.dto.comment.CommentResponse;
import com.tr.schedule.repository.CommentRepository;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 2025-11-14 : V1 : scheduleId(제출 사양) : commentId, userId 사용
// ~ V2 : 개선.
// commentId - scheduleId
// 필요 기능 : 댓글 생성, 댓글 수정, 댓글 삭제 + 일정에 따른 댓글 출력 -> ScheduleService
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentResponse createComment(Long userId, Long scheduleId, CommentCreateRequest request){
        // 1). 변환
        User user=userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find userId : " + userId));
    }
    public Comment update(Long commentId, Long userId) {}
    public Comment delete(Long commentId, Long userId) {}
    public Comment getAuthorOrElse(Long)
}
