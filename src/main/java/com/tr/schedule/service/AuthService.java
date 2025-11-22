package com.tr.schedule.service;


import com.tr.schedule.domain.RefreshToken;
import com.tr.schedule.dto.auth.*;
import com.tr.schedule.dto.user.UserSummaryResponse;
import com.tr.schedule.global.exception.*;
import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.User;
import com.tr.schedule.global.security.CustomUserDetails;
import com.tr.schedule.global.security.JwtTokenProvider;
import com.tr.schedule.repository.RefreshTokenRepository;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


// UseCase, domain, token : Email, pw validation : token : domain <-> dto : Mapper
// 필요 기능 : 회원 가입, 로그인
// email : principal : 현재 로그인 대상 식별 값.
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
        // 6). 식별
        AuthTokens tokens=issueTokens(user);
        // 7). 압축
        UserSummaryResponse summary = authMapper.toUserSummary(user);
        // 7). 깔쌈하게 슛
        return new SignupResponse(tokens.accessToken(), tokens.refreshToken(), summary);
    }

    // POST
    @Transactional
    public LoginResponse login(LoginRequest request){
        // 1). email로 user 조회.
        User user= findUserByEmailOrThrow(request);
        // 2). Password 검증
        validatePassword(request, user);
        // 3). 식별
        AuthTokens tokens=issueTokens(user);
        // 4). 압축
        UserSummaryResponse summary = authMapper.toUserSummary(user);
        // 5). 깔쌈하게 슛
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), summary);
    }

    // refreshAccessToken
    @Transactional
    public AuthTokens refreshAccessToken(String refreshTokenValue){
        RefreshToken refreshToken=refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(()-> new JwtAuthenticationException(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED)); // 이거 에러코드 몇 번이지 404?
        // token 만료 또는 취소
        if(refreshToken.isExpired()||refreshToken.isRevoked()){
            throw new JwtAuthenticationException(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED); // 만료되어서 찾을 수 없다? 만료되어서 권한이 없다?? 와 어렵네
        }
        // userId
        User user=refreshToken.getUser();

        return issueTokens(user);
    }

    // 정리용 헬퍼 메서드
    private User findUserByEmailOrThrow(LoginRequest request){
        return userRepository.findByEmail(request.getEmail()) // 검사 + 대입
            .orElseThrow(() -> new JwtAuthenticationException(ErrorCode.AUTH_INVALID_CREDENTIALS));
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
    // 로그인 시 RefreshToken 발급 및 저장
    private AuthTokens issueTokens(User user){
        CustomUserDetails principal = new CustomUserDetails(user);

        String access=jwtTokenProvider.generateAccessToken(principal);
        String refreshValue=jwtTokenProvider.generateRefreshToken(principal);

        // 기존의 토큰 정책 분리 : 단순한 방법으론 user 별로 다 지우고 새로 하나만 생성.
        refreshTokenRepository.deleteAllByUserId(user.getId());
        // 또는 deleteAllByUser(user)

        RefreshToken refreshToken=RefreshToken.issue(
            user,
            refreshValue,
            LocalDateTime.now().plusDays(7));
        // 저장
        refreshTokenRepository.save(refreshToken);

        return new AuthTokens(access, refreshValue);
    }


}

/*
회전(rotation) 빌드업
1). 기존 refreshToken.revoked=true
2). 새로운 refresh 토큰 생성 후 저장
3). 새 값을 response로 돌려주면 됨.
*/
