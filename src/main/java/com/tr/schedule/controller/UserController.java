package com.tr.schedule.controller;


import com.tr.schedule.dto.user.ChangeEmailRequest;
import com.tr.schedule.dto.user.ChangePasswordRequest;
import com.tr.schedule.dto.user.UserChangeProfileRequest;
import com.tr.schedule.dto.user.UserSummaryResponse;
import com.tr.schedule.global.security.AuthUser;
import com.tr.schedule.global.security.CurrentUser;
import com.tr.schedule.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


// Controller 요청이 반드시 1대1일 필요는 없긴 하네. 다만, 스파게티 코드를 유발하기 쉬운 구조로 보임.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 내 정보
    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getMyProfile(@AuthUser CurrentUser currentUser) {
        return ResponseEntity.ok(userService.getProfile(currentUser.id()));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthUser CurrentUser currentUser, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser.id(), request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/me/email")
    public ResponseEntity<Void> changeEmail(@AuthUser CurrentUser currentUser, @Valid @RequestBody ChangeEmailRequest request) {
        userService.changeEmail(currentUser.id(), request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/me/profile")
    public ResponseEntity<Void> changeProfile(@AuthUser CurrentUser currentUser, @Valid @RequestBody UserChangeProfileRequest request) {
        userService.changeProfile(currentUser.id(), request);
        return ResponseEntity.noContent().build();
    }

    // 특정 유저 정보 조회 (관리자 or 공개범위)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserSummaryResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
