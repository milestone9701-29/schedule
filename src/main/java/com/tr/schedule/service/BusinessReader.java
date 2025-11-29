package com.tr.schedule.service;

import com.tr.schedule.domain.Comment;
import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import com.tr.schedule.global.exception.BusinessException;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.ResourceNotFoundException;
import com.tr.schedule.repository.CommentRepository;
import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessReader {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    public User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
    public Schedule getScheduleOrThrow(Long scheduleId){
        return scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
    }
    public Comment getCommentOrThrow(Long commentId){
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

}
