package com.tr.schedule.controller;


import com.tr.schedule.domain.User;
import com.tr.schedule.dto.auth.AuthMapper;
import com.tr.schedule.dto.auth.LoginRequest;
import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.dto.auth.UserResponse;
import com.tr.schedule.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 회원 가입, 로그인 : POST
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController{

    private final UserService userService;
    private final AuthMapper authMapper;

    @PostMapping
    public ResponseEntity<UserResponse> signUp(SignupRequest request){
        User saved=userService.signUp(request); // 정리
        return ResponseEntity.status(HttpStatus.CREATED).body(authMapper.toResponse(saved));
    }

    @GetMapping // 일치 여부 체크만 하면 되니까 Get이지.
    public ResponseEntity<UserResponse> login(LoginRequest request){
        User saved=userService.login(request); // 정리
        return ResponseEntity.status(HttpStatus.OK).body(authMapper.toResponse(saved));
    }
}
