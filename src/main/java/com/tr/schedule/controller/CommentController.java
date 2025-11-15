package com.tr.schedule.controller;


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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/schedules/{scheduleId}")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long userId, @PathVariable Long scheduleId,
                                                         @Valid @RequestBody CommentCreateRequest request){
        CommentResponse response=commentService.createComment(userId, scheduleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping(("/users/{userId}/comments/{commentId}"))
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request){
        CommentResponse response=commentService.updateComment(userId, commentId, request);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long userId, @PathVariable Long commentId){
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<List<CommentResponse>> listComments(@PathVariable Long scheduleId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.listCommentsBySchedule(scheduleId));
    }

}
