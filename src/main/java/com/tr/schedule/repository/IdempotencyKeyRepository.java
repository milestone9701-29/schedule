package com.tr.schedule.repository;

import com.tr.schedule.domain.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByIdempotencyKeyAndUserId(String key, Long userId); // 2025-11-18 : Key - UserId 찾기
}
