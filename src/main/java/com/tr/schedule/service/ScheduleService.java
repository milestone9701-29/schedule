package com.tr.schedule.service;


import com.tr.schedule.common.exception.BusinessAccessDeniedException;
import com.tr.schedule.common.exception.ErrorCode;
import com.tr.schedule.common.exception.ResourceNotFoundException;
import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.dto.schedule.ScheduleResponse;
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
import com.tr.schedule.dto.schedule.ScheduleMapper;

import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request){
        // 1). 변환.
        User user = getUserOrThrow(userId);
        // 2). dto : MapperClass 사용.
        Schedule schedule = scheduleMapper.toScheduleEntity(user, request);
        // 3). 실제 저장
        Schedule saved = scheduleRepository.save(schedule);
        // 4). 반환
        return scheduleMapper.toScheduleResponse(saved);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request){
        // 1). 변환
        User user = getUserOrThrow(userId);
        Schedule schedule = getScheduleOrThrow(scheduleId);
        // 2). equals
        validateEachOther(user, schedule);
        // 3). 실제 내용 수정
        schedule.scheduleUpdate(request.getTitle(), request.getContent());
        // 4). 저장 : 트랜잭션을 쓰면 여기서 save 안 해도 됨(@Transactional),
        Schedule saved = scheduleRepository.save(schedule);
        // 5). 반환
        return scheduleMapper.toScheduleResponse(saved);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId){
        // 1). 변환
        User user = getUserOrThrow(userId);
        Schedule schedule = getScheduleOrThrow(scheduleId);
        // 2). equals : 정리 예정
        validateEachOther(user, schedule);
        // 3). 실제 내용 : 삭제
        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly=true)
    public Page<ScheduleResponse> listUserSchedules(Long userId, Pageable pageable){
        // 1). 변환 : owner
        User owner = getUserOrThrow(userId);
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
    private void validateEachOther(User user, Schedule schedule){
        if (!user.getId().equals(schedule.getOwner().getId())) {
            throw new BusinessAccessDeniedException(ErrorCode.SCHEDULE_FORBIDDEN);
        }
    }
}
