package com.tr.schedule.controller;

import com.tr.schedule.global.security.CurrentUser;
import com.tr.schedule.global.security.CustomUserDetails;
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
    public ResponseEntity<ScheduleResponse> createSchedule(@AuthenticationPrincipal CustomUserDetails principal,
                                                           @Valid @RequestBody ScheduleCreateRequest request,
                                                           @RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey){
        CurrentUser currentUser=CurrentUser.from(principal); // User Check

        ScheduleResponse response=scheduleService.createSchedule(currentUser, request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@AuthenticationPrincipal CustomUserDetails principal,
                                                           @PathVariable Long scheduleId,
                                                           @Valid @RequestBody ScheduleUpdateRequest request){
        CurrentUser currentUser=CurrentUser.from(principal); // User Check

        ScheduleResponse response=scheduleService.updateSchedule(currentUser, scheduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@AuthenticationPrincipal CustomUserDetails principal,
                                               @PathVariable Long scheduleId){
        CurrentUser currentUser=CurrentUser.from(principal);

        scheduleService.deleteSchedule(currentUser, scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        // return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
    // 내 일정 : List 변환으로 데이터 다루는 테크닉 보완 필요.
    @GetMapping("/me")
    public ResponseEntity<Page<ScheduleResponse>> listUserSchedules(@AuthenticationPrincipal CustomUserDetails principal,
                                                          @PageableDefault
                                                              (size=10, sort={"updatedAt"}, direction= Sort.Direction.DESC) Pageable pageable){
        CurrentUser currentUser=CurrentUser.from(principal);

        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.listUserSchedules(currentUser, pageable));
    }
    // 전체
    @GetMapping
    public ResponseEntity<Page<ScheduleResponse>> listSchedules(@PageableDefault
                                                                        (size=10, sort={"updatedAt"}, direction= Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.listSchedules(pageable));
    }

}
