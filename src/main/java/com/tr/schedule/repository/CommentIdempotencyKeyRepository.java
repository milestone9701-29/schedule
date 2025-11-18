package com.tr.schedule.repository;

import com.tr.schedule.domain.CommentIdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentIdempotencyKeyRepository extends JpaRepository<CommentIdempotencyKey, Long>{
    Optional<CommentIdempotencyKey> findByKeyAndUserIdAndScheduleId(String key, Long userId, Long scheduleId);
    // 2025-11-18 : CommentIdempotencyKey - UserId - ScheduleId 사용

}
