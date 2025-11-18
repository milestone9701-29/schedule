package com.tr.schedule.dto.schedule;

import com.tr.schedule.domain.Schedule;


import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleMapper{
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
    public List<ScheduleResponse> toScheduleResponseList(List<Schedule> schedules) {
        return schedules.stream()
            .map(this::toScheduleResponse)
            .toList();
    }
}
