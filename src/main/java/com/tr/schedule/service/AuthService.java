package com.tr.schedule.service;


import com.tr.schedule.dto.auth.*;
import com.tr.schedule.dto.user.UserSummaryResponse;
import com.tr.schedule.global.exception.BusinessException;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.PasswordMismatchException;
import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.User;
import com.tr.schedule.global.exception.ResourceNotFoundException;
import com.tr.schedule.global.security.CustomUserDetails;
import com.tr.schedule.global.security.JwtTokenProvider;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



// UseCase, domain, token : Email, pw validation : token : domain <-> dto : Mapper
// 필요 기능 : 회원 가입, 로그인
// email : principal : 현재 로그인 대상 식별 값.
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;

    // POST
    @Transactional
    public SignupResponse signUp(SignupRequest request){
        // 1). EMAIL 중복 체크
        validateEmail(request);
        // 2). PasswordEncoder로 인코딩
        String encodedPassword=passwordEncoder.encode(request.getPassword());
        // 3). Mapper Class로 User Entity 생성.
        User user = authMapper.ofSignUp(encodedPassword, request);
        // 4). 가입 시 Role 부여.
        user.addRole(Role.USER);
        // 5). userRepository에 user 저장 후 반환.
        userRepository.save(user);
        // 6). 압축
        UserSummaryResponse summary = authMapper.toUserSummary(user);
        // 7). 깔쌈하게 슛
        return new SignupResponse(summary);
    }

    // POST
    @Transactional
    public LoginResponse login(LoginRequest request){
        // 1). email로 user 조회.
        User user= findUserByEmailOrThrow(request);
        // 2). Password 검증
        validatePassword(request, user);
        // 3). 식별
        CustomUserDetails principal = new CustomUserDetails(user);
        AuthTokens tokens=jwtTokenProvider.generateTokens(principal);
        // 4). 압축
        UserSummaryResponse summary = authMapper.toUserSummary(user);
        // 5). 깔쌈하게 슛
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), summary);
    }

    // 정리용 헬퍼 메서드
    private User findUserByEmailOrThrow(LoginRequest request){
        return userRepository.findByEmail(request.getEmail()) // 검사 + 대입
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.AUTH_INVALID_CREDENTIALS));
    }
    private void validateEmail(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){ // 검사
            throw new BusinessException(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
        }
    }
    private void validatePassword(LoginRequest request, User user){
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){ // matches로 검증
            throw new PasswordMismatchException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }
}
