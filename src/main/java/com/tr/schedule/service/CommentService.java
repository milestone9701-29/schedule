package com.tr.schedule.service;


import com.tr.schedule.common.exception.BusinessAccessDeniedException;
import com.tr.schedule.common.exception.ResourceNotFoundException;
import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.comment.CommentCreateRequest;
import com.tr.schedule.dto.comment.CommentResponse;
import com.tr.schedule.dto.comment.CommentMapper;
import com.tr.schedule.dto.comment.CommentUpdateRequest;
import com.tr.schedule.repository.CommentRepository;
import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 2025-11-14 : V1 : scheduleId(제출 사양) : commentId, userId 사용
// ~ V2 : 개선.
// commentId - scheduleId
// 필요 기능 : 댓글 생성, 댓글 수정, 댓글 삭제 + 일정에 따른 댓글 출력 -> ScheduleService
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse createComment(Long userId, Long scheduleId, CommentCreateRequest request){
        // 1). 변환
        User user=getUserOrThrow(userId);
        Schedule schedule=getScheduleOrThrow(scheduleId);
        // 2). dto : MapperClass 사용.
        Comment comment=commentMapper.toCommentEntity(user, schedule, request);
        // 3). 실제 저장
        Comment saved=commentRepository.save(comment);
        // 4). 반환.
        return commentMapper.toCommentResponse(saved);
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        // 1). 변환
        User user=getUserOrThrow(userId);
        Comment comment=getCommentOrThrow(commentId);
        // 2). equals
        validateEachOther(user, comment);
        // 3). 실제 갱신
        comment.commentUpdate(request.getContent());
        // 4). 저장.
        Comment saved = commentRepository.save(comment);
        // 5). 반환.
        return commentMapper.toCommentResponse(saved);
    }
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        // 1). 변환
        User user=getUserOrThrow(userId);
        Comment comment=getCommentOrThrow(commentId);
        // 2). equals
        validateEachOther(user, comment);
        // 3). 삭제
        commentRepository.delete(comment);
    }
    @Transactional(readOnly=true)
    public List<CommentResponse> listCommentsBySchedule(Long scheduleId){
        List<Comment> saved = commentRepository.findBySchedule_IdOrderByCreatedAtAsc(scheduleId);
        return commentMapper.toCommentResponseList(saved);
    }

    // 정리용 헬퍼 메서드
    private User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Cannot find userId : " + userId));
    }
    private Schedule getScheduleOrThrow(Long scheduleId){
        return scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Cannot find scheduleId : " + scheduleId));
    }
    private Comment getCommentOrThrow(Long commentId){
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Cannot find commentId : " + commentId));
    }
    private void validateEachOther(User user, Comment comment){
        if (!user.getId().equals(comment.getAuthor().getId())) { throw new BusinessAccessDeniedException("ID 불일치"); }
        }
}
