package com.tr.schedule.dto.admin;

import com.tr.schedule.domain.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record AdminScheduleResponse(
    Long scheduleId,
    String title,
    String ownerEmail,
    Set<Role> ownerRoles,
    boolean deleted,
    LocalDateTime createdAt
) {

}
