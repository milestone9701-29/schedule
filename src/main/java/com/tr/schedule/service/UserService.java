package com.tr.schedule.service;

import com.tr.schedule.domain.User;
import com.tr.schedule.dto.auth.AuthMapper;
import com.tr.schedule.dto.auth.LoginRequest;
import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 필요 기능 : 회원 가입, 로그인
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    public User signUp(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){ // 검사
            throw new IllegalArgumentException("Email already exists");
        }
        String encodedPassword=passwordEncoder.encode(request.getPassword());

        User user = authMapper.ofSignUp(encodedPassword, request);

        return userRepository.save(user);
    }
    public User login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail()) // 검사 + 대입
            .orElseThrow(() -> new IllegalArgumentException("Invalid Email"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){ // matches로 검증
            throw new IllegalArgumentException("Invalid Password");
        }
        return user;
    }
}
