package com.tr.schedule.repository;

import com.tr.schedule.domain.Schedule;
import com.tr.schedule.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


// 작성 기준은 schedule Entity 명칭 기준.
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Page<Schedule> findAllByOwnerOrderByUpdatedAtDesc(User owner, Pageable pageable); // user id 갱신일자 내림차순으로 정렬
    Page<Schedule> findAllByOrderByUpdatedAtDesc(Pageable pageable); // 갱신일자 내림차순 정렬.
}
