package com.tr.schedule.controller;


import com.tr.schedule.common.security.CustomUserDetails;
import com.tr.schedule.dto.user.UserProfileResponse;
import com.tr.schedule.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// Controller 요청이 반드시 1대1일 필요는 없긴 하네. 다만, 스파게티 코드를 유발하기 쉬운 구조로 보임.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 내 정보
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {

        return ResponseEntity.ok(userService.getProfile(currentUser.getId()));
    }

    // 특정 유저 정보 조회 (관리자 or 공개범위)
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
