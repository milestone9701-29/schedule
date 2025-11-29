package com.tr.schedule.repository;

import com.tr.schedule.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{
    Optional<RefreshToken> findByToken(String token); // 토큰 찾기
    void deleteAllByUser_Id(Long userId); // userId로 조회 ->삭제
    Optional<RefreshToken> findByTokenAndUser_Id(String refreshToken, Long userId);
}
