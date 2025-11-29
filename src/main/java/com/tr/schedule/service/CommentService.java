package com.tr.schedule.service;

import com.tr.schedule.domain.policy.AccessPolicy;
import com.tr.schedule.global.security.CurrentUser;
import com.tr.schedule.domain.*;
import com.tr.schedule.dto.comment.CommentCreateRequest;
import com.tr.schedule.dto.comment.CommentResponse;
import com.tr.schedule.dto.comment.CommentMapper;
import com.tr.schedule.dto.comment.CommentUpdateRequest;
import com.tr.schedule.repository.*;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
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
    private final CommentMapper commentMapper;
    private final CommentIdempotencyKeyRepository commentIdempotencyKeyRepository;
    private final BusinessReader businessReader;

    // -- Service Method Lv.3 : CurrentUser + id + Req DTO + idempotencyKey -- //
    @Transactional
    public CommentResponse createComment(CurrentUser currentUser,
                                         Long scheduleId,
                                         CommentCreateRequest request,
                                         @Nullable String commentIdempotencyKey
    ){
        // 1). 멱등성 키가 있으면 조회. 없으면 일반 POST처럼 동작.
        if(hasIdempotencyKey(commentIdempotencyKey)){
            Optional<CommentResponse> existing=findExistingCommentResponse(currentUser, scheduleId, commentIdempotencyKey);
            if(existing.isPresent()){ // 있으면
                return existing.get(); // 재사용
            }
        }
        // 2). 실제 스케쥴 생성(save)
        Comment comment = createNewComment(scheduleId, currentUser, request);
        // 3). 멱등성 키 저장
        if(hasIdempotencyKey(commentIdempotencyKey)){
            registerCommentIdempotencyKey(commentIdempotencyKey, currentUser, scheduleId, comment.getId());
        }
        // 4). 응답 반환
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional
    public CommentResponse updateComment(CurrentUser currentUser,
                                         Long scheduleId,
                                         Long commentId, CommentUpdateRequest request) {
        // 1). 변환
        Schedule schedule=businessReader.getScheduleOrThrow(scheduleId);
        Comment comment=businessReader.getCommentOrThrow(commentId);
        // 2). equals
        AccessPolicy.ensureCanAccessComment(schedule, currentUser, comment);
        // 3). 실제 갱신
        comment.update(request.getContent(), request.getVersion());
        // 4). 저장.
        // 5). 반환.
        return commentMapper.toCommentResponse(comment);
    }
    @Transactional
    public void deleteComment(CurrentUser currentUser, Long scheduleId, Long commentId) {
        // 1). 변환
        Schedule schedule=businessReader.getScheduleOrThrow(scheduleId);
        Comment comment=businessReader.getCommentOrThrow(commentId);
        // 2). equals
        AccessPolicy.ensureCanAccessComment(schedule, currentUser, comment);
        // 3). 삭제
        commentRepository.delete(comment);
    }
    @Transactional(readOnly=true)
    public List<CommentResponse> listCommentsBySchedule(Long scheduleId){
        List<Comment> saved = commentRepository.findBySchedule_IdOrderByCreatedAtAsc(scheduleId);
        return commentMapper.toCommentResponseList(saved);
    }

    // -------------------------------------------- HELPER : Lv.2 -------------------------------------------- //
    private Comment createNewComment(Long scheduleId, CurrentUser currentUser, CommentCreateRequest request){
        User owner = businessReader.getUserOrThrow(currentUser.id());
        Schedule schedule =  businessReader.getScheduleOrThrow(scheduleId);
        Comment comment = Comment.of(schedule, owner, request.getContent());
        return commentRepository.save(comment);
    }

    // ----------------- IdempotencyKey ----------------- //
    private boolean hasIdempotencyKey(@Nullable String idempotencyKey){ // 멱등키 체크 : null, 공백 검사
        return idempotencyKey!=null&&!idempotencyKey.isBlank();
    }
    private Optional<CommentResponse> findExistingCommentResponse(CurrentUser currentUser, Long scheduleId, String commentIdempotencyKey){
        return commentIdempotencyKeyRepository
            .findByIdempotencyKeyAndUserIdAndScheduleId(commentIdempotencyKey, currentUser.id(), scheduleId) // Optional<CommentIdempotencyKey>
            .map(CommentIdempotencyKey::getCommentId)  // key -> key.getCommentId(). : Optional<CommentIdempotencyKey> -> Optional<Long> : commentId
            .map(this.businessReader::getCommentOrThrow) // id -> this.businessReader.getCommentOrThrow(id) : Optional<Long> -> Optional<Comment>
            .map(commentMapper::toCommentResponse); // Optional<Comment> -> Optional<CommentResponse>
    }
    private void registerCommentIdempotencyKey(String commentIdempotencyKey, CurrentUser currentUser, Long scheduleId, Long commentId){
        CommentIdempotencyKey entity=CommentIdempotencyKey.of(commentIdempotencyKey, currentUser.id(), scheduleId, commentId);
        commentIdempotencyKeyRepository.save(entity);
    }
}
/* isAdmin() || currentUser.isManager() */
