package com.tr.schedule.controller;


import com.tr.schedule.dto.admin.AdminUserDetailResponse;
import com.tr.schedule.dto.admin.AdminUserSummaryResponse;
import com.tr.schedule.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users/")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PutMapping("{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId){
        adminUserService.banUser(userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId){
        adminUserService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{userId}/detail")
    public ResponseEntity<AdminUserDetailResponse> getDetailUserInfo(@PathVariable Long userId){
        AdminUserDetailResponse body=adminUserService.getDetailUserInfo(userId);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("{userId}/summary")
    public ResponseEntity<AdminUserSummaryResponse> getSummaryUserInfo(@PathVariable Long userId){
        AdminUserSummaryResponse body=adminUserService.getSummaryUserInfo(userId);
        return ResponseEntity.ok().body(body);
    }

    @PostMapping("{userId}/roles/admin")
    public ResponseEntity<Void> grantAdmin(@PathVariable Long userId){
        adminUserService.grantAdmin(userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("{userId}/roles/admin/revoke")
    public ResponseEntity<Void> revokeAdmin(@PathVariable Long userId){
        adminUserService.revokeAdmin(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{userId}/roles/manager")
    public ResponseEntity<Void> grantManager(@PathVariable Long userId){
        adminUserService.grantManager(userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("{userId}/roles/manager/revoke")
    public ResponseEntity<Void> revokeManager(@PathVariable Long userId){
        adminUserService.revokeManager(userId);
        return ResponseEntity.noContent().build();
    }
}
