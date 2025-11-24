package com.tr.schedule.controller;


import com.tr.schedule.global.security.AuthUser;
import com.tr.schedule.global.security.CurrentUser;
import com.tr.schedule.dto.comment.CommentCreateRequest;

import com.tr.schedule.dto.comment.CommentResponse;
import com.tr.schedule.dto.comment.CommentUpdateRequest;
import com.tr.schedule.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Http Header : Kebab-Case
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules/{scheduleId}/comments") // 댓글은 스케쥴에 종속하기 때문.
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@AuthUser CurrentUser currentUser,
                                                         @PathVariable Long scheduleId,
                                                         @Valid @RequestBody CommentCreateRequest request,
                                                         @RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey){


        CommentResponse response=commentService.createComment(currentUser, scheduleId, request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@AuthUser CurrentUser currentUser,
                                                         @PathVariable Long scheduleId,
                                                         @PathVariable Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request){

        CommentResponse response=commentService.updateComment(currentUser, scheduleId ,commentId, request);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthUser CurrentUser currentUser,
                                              @PathVariable Long scheduleId,
                                              @PathVariable Long commentId){

        commentService.deleteComment(currentUser, scheduleId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> listComments(@PathVariable Long scheduleId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.listCommentsBySchedule(scheduleId));
    }

}
