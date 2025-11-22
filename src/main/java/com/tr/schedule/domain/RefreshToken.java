package com.tr.schedule.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Table(name="refresh_tokens", // DDL : Index 정의
    uniqueConstraints=@UniqueConstraint(name = "uk_refresh_tokens_token", columnNames="token"),
    indexes={@Index(name="idx_refresh_tokens_user_id", columnList="user_id")})
public class RefreshToken extends BaseTimeEntity {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name="token", nullable = false, unique = true, length = 500)
    private String token;

    // 만료일
    @Column(name="expires_at", nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable=false)
    private boolean revoked=false;



    @Builder(access=AccessLevel.PRIVATE)
    private RefreshToken(User user, String token, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken issue(User user, String token, LocalDateTime expiresAt) {
        return new RefreshToken(user, token, expiresAt);
    }

    // 만료 됐는지?
    public boolean isExpired(){
        return expiresAt.isBefore(LocalDateTime.now());
    }
    // 취소할거니
    public void revoke(){
        this.revoked=true;
    }
}
/*
* private String userAgent;

private String ip;

private String deviceName;*/
