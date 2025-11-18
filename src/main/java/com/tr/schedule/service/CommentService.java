package com.tr.schedule.service;


import com.tr.schedule.common.exception.BusinessAccessDeniedException;
import com.tr.schedule.common.exception.ErrorCode;
import com.tr.schedule.common.exception.ResourceNotFoundException;
import com.tr.schedule.common.security.CurrentUser;
import com.tr.schedule.domain.*;
import com.tr.schedule.dto.comment.CommentCreateRequest;
import com.tr.schedule.dto.comment.CommentResponse;
import com.tr.schedule.dto.comment.CommentMapper;
import com.tr.schedule.dto.comment.CommentUpdateRequest;
import com.tr.schedule.repository.CommentRepository;
import com.tr.schedule.repository.IdempotencyKeyRepository;
import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public CommentResponse createComment(@AuthenticationPrincipal CurrentUser currentUser,
                                         Long scheduleId,
                                         CommentCreateRequest request,
                                         @Nullable String idempotencyKey
    ){
        // 1). 멱등성 키가 있으면 조회
        if(idempotencyKey!=null&&!idempotencyKey.isBlank()){
            Optional<IdempotencyKey> existing=idempotencyKeyRepository.findByKeyAndUserId(idempotencyKey, currentUser.id());

            if(existing.isPresent()){
                Long commentId=existing.get().getSchedule(scheduleId).getCommentId();
                Comment comment=getCommentOrThrow(commentId);
                return commentMapper.toCommentResponse(comment);
            }
        }
        // 1). 변환
        User author=getUserOrThrow(currentUser.id());
        Schedule schedule=getScheduleOrThrow(scheduleId);
        // 2). MapperClass
        Comment comment=Comment.of(schedule, author, request.getContent());
        // 3). 실제 저장
        commentRepository.save(comment);
        // 4). 반환.
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long scheduleId, Long commentId, CommentUpdateRequest request) {
        // 1). 변환
        User author=getUserOrThrow(userId);
        Schedule schedule=getScheduleOrThrow(scheduleId);
        Comment comment=getCommentOrThrow(commentId);
        // 2). equals
        validateEachOther(schedule, author, comment);
        // 3). 실제 갱신
        comment.update(request.getContent());
        // 4). 저장.
        // 5). 반환.
        return commentMapper.toCommentResponse(comment);
    }
    @Transactional
    public void deleteComment(Long userId, Long scheduleId, Long commentId) {
        // 1). 변환
        User author=getUserOrThrow(userId);
        Schedule schedule=getScheduleOrThrow(scheduleId);
        Comment comment=getCommentOrThrow(commentId);
        // 2). equals
        validateEachOther(schedule, author, comment);
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
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BAD_REQUEST));
    }
    private Schedule getScheduleOrThrow(Long scheduleId){
        return scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BAD_REQUEST));
    }
    private Comment getCommentOrThrow(Long commentId){
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BAD_REQUEST));
    }

    // 정합성..
    private void validateEachOther(Schedule schedule, CurrentUser currentUser, Comment comment) {
        // ADMIN, MANAGER : 같은 Schedule 안의 Comment이면, 누구의 것이든 수정 및 삭제가 가능.
        if (currentUser.isAdmin() || currentUser.isManager()) {
            if (!schedule.getId().equals(comment.getSchedule().getId())) {
                throw new BusinessAccessDeniedException(ErrorCode.BAD_REQUEST);
            }
            return;
        }
        // USER : 본인 댓글 및 스케줄 일치 : 정합성을 위해 남겨둠.
        if (!currentUser.id().equals(comment.getAuthor().getId())) { // 저자 체크
            throw new BusinessAccessDeniedException(ErrorCode.BAD_REQUEST);
        }
        if (!schedule.getId().equals(comment.getSchedule().getId())) { // schedule 간의 id 체크
            throw new BusinessAccessDeniedException(ErrorCode.BAD_REQUEST);
        }
    }
}
