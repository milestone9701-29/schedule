package com.tr.schedule.service;


import com.tr.schedule.common.exception.ErrorCode;
import com.tr.schedule.common.exception.ResourceNotFoundException;

import com.tr.schedule.common.security.CurrentUser;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.user.UserMapper;
import com.tr.schedule.dto.user.UserProfileResponse;
import com.tr.schedule.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly=true)
    public UserProfileResponse getProfile(Long userId) {
        User user = getUserOrThrow(userId);

        return userMapper.toUserProfileResponse(user);
    }

    // 정리용 헬퍼 메서드
    private User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SCHEDULE_FORBIDDEN));
    }
}
