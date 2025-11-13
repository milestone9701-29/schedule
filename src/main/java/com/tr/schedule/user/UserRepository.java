package com.tr.schedule.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 찾기
    boolean existsByEmail(String email); // 조건을 만족하는 데이터 찾을 경우 종료.
}
