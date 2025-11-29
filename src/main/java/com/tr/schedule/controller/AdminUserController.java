package com.tr.schedule.controller;


import com.tr.schedule.dto.admin.AdminUserDetailResponse;
import com.tr.schedule.dto.admin.AdminUserSummaryResponse;
import com.tr.schedule.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users/{userId}")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PutMapping("/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId){
        adminUserService.banUser(userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId){
        adminUserService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detail")
    public ResponseEntity<AdminUserDetailResponse> getDetailUserInfo(@PathVariable Long userId){
        AdminUserDetailResponse body=adminUserService.getDetailUserInfo(userId);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/summary")
    public ResponseEntity<AdminUserSummaryResponse> getSummaryUserInfo(@PathVariable Long userId){
        AdminUserSummaryResponse body=adminUserService.getSummaryUserInfo(userId);
        return ResponseEntity.ok().body(body);
    }

    @PostMapping("/roles/admin")
    public ResponseEntity<Void> grantAdminRole(@PathVariable Long userId, @RequestBody GrantRoleRequest request){

    }
}
