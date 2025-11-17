package com.tr.schedule.controller;


import com.tr.schedule.common.security.CustomUserDetails;
import com.tr.schedule.dto.comment.CommentCreateRequest;

import com.tr.schedule.dto.comment.CommentResponse;
import com.tr.schedule.dto.comment.CommentUpdateRequest;
import com.tr.schedule.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules/{scheduleId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                         @PathVariable Long scheduleId,
                                                         @Valid @RequestBody CommentCreateRequest request){
        CommentResponse response=commentService.createComment(currentUser.getId(), scheduleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                         @PathVariable Long scheduleId,
                                                         @PathVariable Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request){
        CommentResponse response=commentService.updateComment(currentUser.getId(), scheduleId ,commentId, request);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails currentUser,
                                              @PathVariable Long scheduleId,
                                              @PathVariable Long commentId){
        commentService.deleteComment(currentUser.getId(), scheduleId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> listComments(@PathVariable Long scheduleId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.listCommentsBySchedule(scheduleId));
    }

}
