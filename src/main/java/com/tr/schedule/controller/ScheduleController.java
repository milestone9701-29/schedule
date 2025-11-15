package com.tr.schedule.controller;


import com.tr.schedule.domain.Schedule;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.dto.schedule.ScheduleMapper;
import com.tr.schedule.dto.schedule.ScheduleResponse;
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
import com.tr.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    public ResponseEntity<ScheduleResponse> createSchedule(@PathVariable Long userId, @Valid @RequestBody ScheduleCreateRequest request){
        ScheduleResponse response=scheduleService.createSchedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long userId, @PathVariable Long scheduleId,
                                                           @Valid @RequestBody ScheduleUpdateRequest request){
        ScheduleResponse response=scheduleService.updateSchedule(userId, scheduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<Void> deleteSchedule(@PathVariable Long userId, @PathVariable Long scheduleId){
        scheduleService.deleteSchedule(userId, scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        // return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    public ResponseEntity<Page<ScheduleResponse>> listUserSchedules(@PathVariable Long userId,
                                                          @PageableDefault(size=10, sort={"updatedAt"}, direction= Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.명칭어떻게하지(userId, pageable).map(ScheduleMapper::toResponse));
    }
}
