package com.tr.schedule.controller;


import com.tr.schedule.global.security.CustomUserDetails;
// import com.tr.schedule.common.security.JwtAuthenticationFilter;
import com.tr.schedule.global.security.JwtTokenProvider;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.auth.*;
import com.tr.schedule.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// 회원 가입, 로그인 : POST
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController{

    private final AuthService authService;
    private final AuthMapper authMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignupRequest request){
        User saved=authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authMapper.toUserResponse(saved));
    }

    @PostMapping("/login") // token
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        User user=authService.login(request);
        CustomUserDetails userDetails=new CustomUserDetails(user);
        String token=jwtTokenProvider.generateToken(userDetails);
        UserResponse userResponse=authMapper.toUserResponse(user);
        return ResponseEntity.ok(new AuthResponse(token, userResponse));

        // return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(token, userResponse));
    }
}
