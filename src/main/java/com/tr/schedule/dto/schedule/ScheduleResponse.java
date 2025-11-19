package com.tr.schedule.dto.schedule;


import lombok.Value;

import java.time.LocalDateTime;

// 스케쥴 생성 시 출력 값 : id, owner, title, content, createdAt, updatedAt
@Value
public class ScheduleResponse {
    Long id;
    Long ownerId;
    String ownerName;
    String title;
    String content;
    Long version; // Lock.
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
