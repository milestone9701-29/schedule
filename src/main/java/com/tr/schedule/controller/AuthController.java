package com.tr.schedule.controller;

import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.dto.auth.UserResponse;
import com.tr.schedule.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 회원 가입, 로그인 : POST
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class AuthController{

    private final UserService userService;

    public ResponseEntity<UserResponse> signUp(SignupRequestequest request){

    }




}
