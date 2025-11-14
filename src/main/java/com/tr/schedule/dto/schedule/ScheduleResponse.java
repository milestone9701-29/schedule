package com.tr.schedule.dto.schedule;


import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import lombok.Value;

import java.time.LocalDateTime;

//
// 스케쥴 생성 시 출력 값 : id, owner, title, content, createdAt, updatedAt
@Value
public class ScheduleResponse {
    Long id;
    User owner;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // 정적 팩토리 메서드
    public static ScheduleResponse from(Schedule s){
        return new ScheduleResponse(
            s.getId(),
            s.getOwner(),
            s.getTitle(),
            s.getContent(),
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}
