package com.tr.schedule.service;


import com.tr.schedule.common.exception.BusinessAccessDeniedException;
import com.tr.schedule.common.exception.ErrorCode;
import com.tr.schedule.common.exception.ResourceNotFoundException;

import com.tr.schedule.common.security.CurrentUser;
import com.tr.schedule.domain.IdempotencyKey;
import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.dto.schedule.ScheduleResponse;
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
import com.tr.schedule.dto.schedule.ScheduleMapper;

import com.tr.schedule.repository.IdempotencyKeyRepository;
import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


// id 매개 변수 기준 : 이미 있는 것을 조작하기 위해 id가 필요
// 새로 만드는 경우, 조건으로 여러 가지 가져오는 경우는 id가 필요 없다.
// 부모 : userId 자식 : scheduleId
// 필요 기능 : 일정 생성, 일정 수정, 일정 삭제, 단건, List 조회 : 기준은 userId 일치로.
// userId -> targetId -> req
// service -> dto 리턴. : 일관성.

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final UserRepository userRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public ScheduleResponse createSchedule(@AuthenticationPrincipal CurrentUser currentUser,
                                           ScheduleCreateRequest request,
                                           @Nullable String idempotencyKey){


        // 1). 멱등성 키가 있으면 조회
        if(idempotencyKey!=null&&!idempotencyKey.isBlank()){
            Optional<IdempotencyKey> existing=idempotencyKeyRepository.findByKeyAndUserId(idempotencyKey, currentUser.id());

            if(existing.isPresent()){
                Long scheduleId=existing.get().getScheduleId();
                Schedule schedule=getScheduleOrThrow(scheduleId);
                return scheduleMapper.toScheduleResponse(schedule);
            }
        }

        // 2). 실제 스케쥴 생성.
        User owner = getUserOrThrow(currentUser.id());
        Schedule schedule = Schedule.of(owner, request.getTitle(), request.getContent());
        // create : save
        scheduleRepository.save(schedule);

        // 3). 멱등성 키 저장
        if(idempotencyKey!=null&&!idempotencyKey.isBlank()){
            IdempotencyKey keyEntity = idempotencyKeyRepository.save(IdempotencyKey.of(idempotencyKey, currentUser.id(), schedule.getId()));
            idempotencyKeyRepository.save(keyEntity);
        }
        // 4). 응답 반환
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(@AuthenticationPrincipal CurrentUser currentUser,
                                           Long scheduleId,
                                           ScheduleUpdateRequest request){
        // 1). 변환
        Schedule schedule = getScheduleOrThrow(scheduleId);
        // 2). equals
        validateEachOther(currentUser, schedule);

        // 3). 실제 내용 수정
        schedule.update(request.getTitle(), request.getContent());
        // 4). 저장 : 트랜잭션을 쓰면 여기서 save 안 해도 됨(@Transactional),
        // 5). 반환
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(@AuthenticationPrincipal CurrentUser currentUser, Long scheduleId){
        // 1). 변환
        Schedule schedule = getScheduleOrThrow(scheduleId);
        // 2). equals : 정리 예정
        validateEachOther(currentUser, schedule);
        // 3). 실제 내용 : 삭제
        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly=true)
    public Page<ScheduleResponse> listUserSchedules(@AuthenticationPrincipal CurrentUser currentUser, Pageable pageable){
        // 1). 변환 : owner
        User owner = getUserOrThrow(currentUser.id());
        // 2). 실제 내용.
        Page<Schedule> page = scheduleRepository.findAllByOwnerOrderByUpdatedAtDesc(owner, pageable);
        // 3). 반환.
       return page.map(scheduleMapper::toScheduleResponse);
    } // if문은 책임, 가독성을 위해 제외.

    @Transactional(readOnly=true)
    public Page<ScheduleResponse> listSchedules(Pageable pageable){
        Page<Schedule> page = scheduleRepository.findAllByOrderByUpdatedAtDesc(pageable);
        return page.map(scheduleMapper::toScheduleResponse);
    }


    // 정리용 헬퍼 메서드.
    private User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
    }
    private Schedule getScheduleOrThrow(Long scheduleId){
        return scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
    }
    private void validateEachOther(CurrentUser currentUser, Schedule schedule){
        // ADMIN, MANAGER : 타인의 일정
        // User Entity로 권한 꺼내는 방법 : if(user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.MANAGER)){ return; }
        // Authorization, 정합성.
        if(currentUser.isAdmin()||currentUser.isManager()){ return; }

        // User : Owner 일치 시에만 허용.
        if (!currentUser.id().equals(schedule.getOwner().getId())) { throw new BusinessAccessDeniedException(ErrorCode.SCHEDULE_FORBIDDEN); }
    }
}
