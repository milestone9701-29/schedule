package com.tr.schedule.service;


import com.tr.schedule.dto.user.*;
import com.tr.schedule.global.exception.BusinessException;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.PasswordMismatchException;

import com.tr.schedule.domain.User;
import com.tr.schedule.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final BusinessReader businessReader;

    @Transactional(readOnly=true)
    public UserProfileResponse getProfile(Long currentUserId) {
        User user = businessReader.getUserOrThrow(currentUserId);

        return userMapper.toUserProfile(user);
    }

    @Transactional
    public UserProfileResponse changeProfile(Long currentUserId, ChangeProfileRequest request){
        User user = businessReader.getUserOrThrow(currentUserId);
        user.changeProfile(request.getUsername(), request.getProfileImageUrl(), request.getBio());

        return userMapper.toUserProfile(user);
    }

    @Transactional
    public void changePassword(Long currentUserId, ChangePasswordRequest request) {

        User user = businessReader.getUserOrThrow(currentUserId);

        validatePassword(request.getCurrentPassword(), user);

        String encoded = passwordEncoder.encode(request.getNewPassword());

        user.changePassword(encoded);
    }

    @Transactional
    public void changeEmail(Long currentUserId, ChangeEmailRequest request) {

        User user = businessReader.getUserOrThrow(currentUserId);

        validatePassword(request.getCurrentPassword(), user);

        validateEmail(request.getNewEmail(), user.getId());

        user.changeEmail(request.getNewEmail());
    }
    private void validatePassword(String currentPassword, User user){
        if(!passwordEncoder.matches(currentPassword, user.getPasswordHash())){ // matches로 검증
            throw new PasswordMismatchException(ErrorCode.USER_PASSWORD_MISMATCH);
        }
    }
    private void validateEmail(String newEmail, Long currentUserId){
        if(userRepository.existsByEmailAndIdNot(newEmail, currentUserId)){ // 검사
            throw new BusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
    }
}
