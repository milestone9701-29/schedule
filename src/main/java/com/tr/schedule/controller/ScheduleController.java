package com.tr.schedule.controller;

import com.tr.schedule.common.security.CustomUserDetails;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// @AuthenticationPrincipal
// 필요 기능 : 일정 생성, 일정 수정, 일정 삭제, 단건, List 조회 : 기준은 userId 일치로.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;


    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@AuthenticationPrincipal CustomUserDetails currentUser, @Valid @RequestBody ScheduleCreateRequest request){
        ScheduleResponse response=scheduleService.createSchedule(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long scheduleId,
                                                           @Valid @RequestBody ScheduleUpdateRequest request){
        ScheduleResponse response=scheduleService.updateSchedule(currentUser.getId(), scheduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long scheduleId){
        scheduleService.deleteSchedule(currentUser.getId(), scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        // return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
    // 내 일정
    @GetMapping("/me")
    public ResponseEntity<Page<ScheduleResponse>> listUserSchedules(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                          @PageableDefault(size=10, sort={"updatedAt"}, direction= Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.listUserSchedules(currentUser.getId(), pageable));
    }
    // 전체
    @GetMapping
    public ResponseEntity<Page<ScheduleResponse>> listSchedules(@PageableDefault(size=10, sort={"updatedAt"}, direction= Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.listSchedules(pageable));
    }
}
