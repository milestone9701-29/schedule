package com.tr.schedule.service;


import com.tr.schedule.common.exception.EmailMismatchException;
import com.tr.schedule.common.exception.ErrorCode;
import com.tr.schedule.common.exception.PasswordMismatchException;
import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.auth.AuthMapper;
import com.tr.schedule.dto.auth.LoginRequest;
import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




// 필요 기능 : 회원 가입, 로그인
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    // POST
    @Transactional
    public User signUp(SignupRequest request){
        if(checkingEmail(request)){ // 검사
            throw new IllegalArgumentException();
        }
        String encodedPassword=passwordEncoder.encode(request.getPassword());

        User user = authMapper.ofSignUp(encodedPassword, request);

        user.addRole(Role.USER); // 가입 시 권한 부여
        return userRepository.save(user);
    }

    // POST
    @Transactional
    public User login(LoginRequest request){
        User user=findLoginEmail(request);
        if(!checkingPassword(request, user)){ // matches로 검증
            throw new PasswordMismatchException(ErrorCode.AUTH_INVALID_PASSWORD);
        }
        return user;
    }

    // 정리용 헬퍼 메서드
    private User findLoginEmail(LoginRequest request){
        return userRepository.findByEmail(request.getEmail()) // 검사 + 대입
            .orElseThrow(() -> new EmailMismatchException(ErrorCode.AUTH_INVALID_EMAIL));
    }
    private boolean checkingEmail(SignupRequest request){
        return userRepository.existsByEmail(request.getEmail());
    }
    private boolean checkingPassword(LoginRequest request, User user){
        return passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
    }
}
