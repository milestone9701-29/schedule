package com.tr.schedule.controller;


import com.tr.schedule.domain.Schedule;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.dto.schedule.ScheduleMapper;
import com.tr.schedule.dto.schedule.ScheduleResponse;
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
import com.tr.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



// @AuthenticationPrincipal
// 필요 기능 : 일정 생성, 일정 수정, 일정 삭제, 단건, List 조회 : 기준은 userId 일치로.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    public ResponseEntity<ScheduleResponse> createSchedule(@PathVariable Long ownerId, @Valid @RequestBody ScheduleCreateRequest request){
        Schedule saved=scheduleService.createSchedule(ownerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleMapper.toResponse(saved));
    }

    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long scheduleId, @PathVariable Long ownerId,
                                                           @Valid @RequestBody ScheduleUpdateRequest request){
        Schedule Saved=
    }

    public ResponseEntity<ScheduleResponse> deleteSchedule(@PathVariable Long scheduleId, @Valid @RequestBody)
}
