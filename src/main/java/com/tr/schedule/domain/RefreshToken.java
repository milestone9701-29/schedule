package com.tr.schedule.domain;

import jakarta.persistence.*;

@Entity
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private Long userId;
    @Column(nullable=false, unique=true, length=500)
    private String token;

    // revoked/isDeleted
}
