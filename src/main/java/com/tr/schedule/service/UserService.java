package com.tr.schedule.service;


import com.tr.schedule.dto.user.UserSummaryResponse;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.ResourceNotFoundException;

import com.tr.schedule.domain.User;
import com.tr.schedule.dto.user.UserMapper;
import com.tr.schedule.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly=true)
    public UserSummaryResponse getProfile(Long userId) {
        User user = getUserOrThrow(userId);

        return userMapper.toUserSummaryResponse(user);
    }

    // 404
    private User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
