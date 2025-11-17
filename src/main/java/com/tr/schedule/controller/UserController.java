package com.tr.schedule.controller;


import com.tr.schedule.dto.auth.UserResponse;
import com.tr.schedule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController{

    private UserService userService;

    /*@GetMapping("/{userId}") // 특정 유저 프로필 조회
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal )*/

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo()

}
