package com.tr.schedule.service;


import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
import com.tr.schedule.dto.schedule.ScheduleMapper;

import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

// id 매개 변수 기준 : 이미 있는 것을 조작하기 위해 id가 필요
// 새로 만드는 경우, 조건으로 여러 가지 가져오는 경우는 id가 필요 없다.
// 부모 : userId 자식 : scheduleId
// 필요 기능 : 일정 생성, 일정 수정, 일정 삭제, 단건, List 조회 : 기준은 userId 일치로.
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    private final UserRepository userRepository;

    public Schedule createSchedule(Long ownerId, ScheduleCreateRequest request){
        // 1). 변환.
        User owner=getOwnerOrElse(ownerId);
        // 2). dto : MapperClass 사용.
        Schedule schedule=scheduleMapper.toEntity(owner, request);
        // 3). 반환.
        return scheduleRepository.save(schedule);
    }
    public Schedule updateSchedule(Long ownerId, Long userId, ScheduleUpdateRequest request){
        // 1). 변환
        User owner=getOwnerOrElse(ownerId);
        // 2). dto : MapperClass

        // 3). 반환

    }
    /* public void deleteSchedule(Long ownerId){
        User owner=getOwnerOrElse(ownerId);
        scheduleRepository.delete(owner);

    }*/

    public User getOwnerOrElse(Long ownerId){
        return userRepository.findById(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find ownerId : " + ownerId);
    }

    public Page<Schedule> listAll(Pageable pageable) {
        return scheduleRepository.findAllByOrderByUpdatedAtDesc(pageable);
    }

    public Page<Schedule> listByUserId(Long userId, Pageable pageable) {
        if(userId==null){ throw new IllegalArgumentException("userId is null"); }

    }


    }


}
