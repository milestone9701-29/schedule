package com.tr.schedule.controller;

import com.tr.schedule.dto.auth.*;
import com.tr.schedule.global.security.AuthUser;
import com.tr.schedule.global.security.CurrentUser;
import com.tr.schedule.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// HTTP 세계 : 상대 코드 결정, 응답 DTO로 리턴.
// 회원 가입, 로그인 : POST
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController{

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signUp(@Valid @RequestBody SignupRequest request){
        SignupResponse body=authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/login") // token
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse body=authService.login(request);
        return ResponseEntity.ok(body);

        // return ResponseEntity.status(HttpStatus.OK).body(new AuthTokens(token, userResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthUser CurrentUser currentUser, @RequestHeader("X-Refresh-Token") String refreshToken){
        authService.logout(currentUser.id(), refreshToken);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refresh(@RequestHeader("X-Refresh-Token") String refreshToken){
        AuthTokens tokens=authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }
}
