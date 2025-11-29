package com.tr.schedule.service;


import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.ResourceNotFoundException;

import com.tr.schedule.global.security.CurrentUser;
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
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final BusinessReader businessReader;

    // -- Service Method Lv.3 : CurrentUser + id + Req DTO + idempotencyKey -- //
    @Transactional
    public ScheduleResponse createSchedule(CurrentUser currentUser,
                                           ScheduleCreateRequest request,
                                           @Nullable String idempotencyKey){
        // 1). 멱등성 키가 있으면 조회. 없으면 일반 POST처럼 동작.
        if(hasIdempotencyKey(idempotencyKey)){
            Optional<ScheduleResponse> existing=findExistingScheduleResponse(currentUser, idempotencyKey);
            if(existing.isPresent()){ // 있으면
                return existing.get(); // 재사용
            }
        }
        // 2). 실제 스케쥴 생성(save)
        Schedule schedule = createNewSchedule(currentUser, request);
        // 3). 멱등성 키 저장
        if(hasIdempotencyKey(idempotencyKey)){
            registerIdempotencyKey(idempotencyKey, currentUser, schedule);
        }
        // 4). 응답 반환
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(CurrentUser currentUser,
                                           Long scheduleId,
                                           ScheduleUpdateRequest request){
        // 1). scheduleId 기반으로 조회
        Schedule schedule = businessReader.getScheduleOrThrow(scheduleId);
        // 2). Authorization - 정합성 체크
        validateAccess(currentUser, schedule);
        // 3). Check Version : DB vs Client : schedule.update(a, b, request.getVersion()) -> if
        // 4). 수정
        schedule.update(request.getTitle(), request.getContent(), request.getVersion());
        // 5). 저장 : 트랜잭션을 쓰면 여기서 save 안 해도 됨(@Transactional),
        // 6). 반환
        return scheduleMapper.toScheduleResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(CurrentUser currentUser, Long scheduleId){
        // 1). 변환
        Schedule schedule = businessReader.getScheduleOrThrow(scheduleId);
        // 2). equals : 정리 예정
        validateAccess(currentUser, schedule);
        // 3). 실제 내용 : 삭제
        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly=true)
    public Page<ScheduleResponse> listUserSchedules(CurrentUser currentUser, Pageable pageable){
        // 1). 변환 : owner
        User owner = businessReader.getUserOrThrow(currentUser.id());
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

    // -------------------------------------------- HELPER : Lv.2 -------------------------------------------- //
    private Schedule createNewSchedule(CurrentUser currentUser, ScheduleCreateRequest request){
        User owner = businessReader.getUserOrThrow(currentUser.id());
        Schedule schedule = Schedule.of(owner, request.getTitle(), request.getContent());
        return scheduleRepository.save(schedule);
    }

    // ----------------- Check Validation ----------------- //
    // Authorization, 정합성.
    private void validateAccess(CurrentUser currentUser, Schedule schedule){
        // ADMIN, MANAGER : 타인의 일정
        // User Entity로 권한 꺼내는 방법 : if(user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.MANAGER)){ return; }
        if(currentUser.isAdmin()||currentUser.isManager()){ return; }
        // User : Owner 일치 시에만 허용.
        if (!currentUser.id().equals(schedule.getOwner().getId())) { throw new BusinessAccessDeniedException(ErrorCode.USER_FORBIDDEN); }
    }

    // ----------------- IdempotencyKey ----------------- //
    private boolean hasIdempotencyKey(@Nullable String idempotencyKey){ // 멱등키 체크 : null, 공백 검사
        return idempotencyKey!=null&&!idempotencyKey.isBlank();
    }
    private Optional<ScheduleResponse> findExistingScheduleResponse(CurrentUser currentUser, String idempotencyKey){
        return idempotencyKeyRepository
            .findByIdempotencyKeyAndUserId(idempotencyKey, currentUser.id()) // 값이 있으면 그 안의 값을 변환, 없으면 Optional.empty() 유지
            .map(IdempotencyKey::getScheduleId) // key -> key.getScheduleId(). : Optional<IdempotencyKey> -> Optional<Long>
            .map(this.businessReader::getScheduleOrThrow) // id->this.businessReader.getScheduleOrThrow(id) : Optional<Long> -> Optional<Schedule>
            .map(scheduleMapper::toScheduleResponse); // Optional<Schedule> -> Optional<ScheduleResponse>
    }
    private void registerIdempotencyKey(String idempotencyKey, CurrentUser currentUser, Schedule schedule){
        IdempotencyKey entity=IdempotencyKey.of(idempotencyKey, currentUser.id(), schedule.getId());
        idempotencyKeyRepository.save(entity);
    }
}
