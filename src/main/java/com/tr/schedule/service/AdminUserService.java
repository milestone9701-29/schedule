package com.tr.schedule.service;


import com.tr.schedule.domain.Role;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.admin.AdminUserDetailResponse;
import com.tr.schedule.dto.admin.AdminUserMapper;
import com.tr.schedule.dto.admin.AdminUserSummaryResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final BusinessReader businessReader;

    @Transactional
    public void banUser(Long userId){
        User user=businessReader.getUserOrThrow(userId);
        user.ban(); // ban -> true
    }
    @Transactional
    public void unbanUser(Long userId){
        User user=businessReader.getUserOrThrow(userId);
        user.unBan();
    }

    @Transactional(readOnly=true)
    public AdminUserDetailResponse getDetailUserInfo(Long userId){
        User user=businessReader.getUserOrThrow(userId);

        return adminUserMapper.toAdminDetail(user);
    }

    @Transactional(readOnly=true)
    public AdminUserSummaryResponse getSummaryUserInfo(Long userId){
        User user=businessReader.getUserOrThrow(userId);

        return adminUserMapper.toAdminSummary(user);
    }

    @Transactional
    public void grantAdmin(Long userId){
        User user=businessReader.getUserOrThrow(userId);
        user.addRole(Role.ADMIN);
    }
    @Transactional
    public void revokeAdmin(Long userId){
        User user=businessReader.getUserOrThrow(userId);
        user.removeRole(Role.ADMIN);
    }

    @Transactional
    public void grantManager(Long userId){
        User user=businessReader.getUserOrThrow(userId);
        user.addRole(Role.MANAGER);
    }
    @Transactional
    public void revokeManager(Long userId){
        User user=businessReader.getUserOrThrow(userId);
        user.removeRole(Role.MANAGER);
    }
}
