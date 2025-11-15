package com.tr.schedule.dto.schedule;

import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;

import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper{
    // CreateRequest + owner(User) -> Schedule Entity
    public Schedule toScheduleEntity(User owner,ScheduleCreateRequest request){
        return Schedule.builder()
            .owner(owner)
            .title(request.getTitle())
            .content(request.getContent())
            .build();
    }
    // Schedule Entity -> ScheduleResponse
    public ScheduleResponse toScheduleResponse(Schedule schedule){
        return new ScheduleResponse(
            schedule.getId(),
            schedule.getOwner().getId(),
            schedule.getOwner().getUsername(),
            schedule.getTitle(),
            schedule.getContent(),
            schedule.getCreatedAt(),
            schedule.getUpdatedAt()
        );
    }
}
