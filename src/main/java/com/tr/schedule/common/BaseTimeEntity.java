package com.tr.schedule.common;

// @Column
import jakarta.persistence.Column;
// auditing(JpaConfig.class)
import jakarta.persistence.EntityListeners;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;

// createdAt
import org.springframework.data.annotation.CreatedDate;
// updatedAt
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter(AccessLevel.PROTECTED) // protected
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity { // 추상 클래스
    @CreatedDate
    @Column(name="created_at", nullable=false, updatable=false) // 최초 생성 : updatable false
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at", nullable=false)
    protected LocalDateTime updatedAt;
}
