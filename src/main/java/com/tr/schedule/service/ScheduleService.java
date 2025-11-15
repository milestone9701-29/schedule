package com.tr.schedule.service;


import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.dto.schedule.ScheduleResponse;
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
import com.tr.schedule.dto.schedule.ScheduleMapper;

import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
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


    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request) {
        // 1). 변환.
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find userId : " + userId));
        // 2). dto : MapperClass 사용.
        Schedule schedule = scheduleMapper.toEntity(user, request);
        // 3). 실제 저장
        Schedule saved = scheduleRepository.save(schedule);
        // 4). 반환
        return scheduleMapper.toResponse(saved);
    }

    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request) {
        // 1). userId -> user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find userId : " + userId));
        // 2). scheduleId -> owner
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find scheduleId : " + scheduleId));
        // 3). equals
        if (!user.getId().equals(schedule.getOwner().getId())) {
            throw new IllegalArgumentException("ID 불일치");
        }
        // 4). 실제 내용 수정
        schedule.updateFrom(request);
        // 5). 저장 : 트랜잭션을 쓰면 여기서 save 안 해도 됨(@Transactional),
        Schedule saved = scheduleRepository.save(schedule);
        // 5). 반환
        return scheduleMapper.toResponse(saved);

    }

    public void deleteSchedule(Long userId, Long scheduleId) {
        // 1). 변환
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find userId : " + userId));
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find scheduleId : " + scheduleId));
        // 2). equals
        if (!user.getId().equals(schedule.getOwner().getId())) {
            throw new IllegalArgumentException("ID 불일치");
        }
        // 3). 실제 내용 : 삭제
        scheduleRepository.deleteById(scheduleId);
    }

    public Page<ScheduleResponse> 명칭어떻게하지(Long userId, Pageable pageable) {
        // 1). 변환
        Schedule schedule = scheduleRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find userId : " + userId));
        // 2). 실제 내용.
        return userRepository.findAllByUserIdOrderByUpdatedAtDesc(user, pageable);
        //
    }
}
