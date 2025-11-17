package com.tr.schedule.repository;

import com.tr.schedule.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Long countBySchedule_Id(Long scheduleId); : id 세기
    List<Comment> findBySchedule_IdOrderByCreatedAtAsc(Long scheduleId); // 생성 오름차순 정렬
}
