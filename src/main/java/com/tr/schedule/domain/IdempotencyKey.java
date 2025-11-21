package com.tr.schedule.domain;

import jakarta.persistence.*;
import lombok.*;


// 내부 인프라 테이블 : -> 관리 권한으로 확대(DTO 추가 등)
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Entity
// 유저마다 같은 Key를 쓸 수 있다 : @Table(uniqueConstraints=@UniqueConstraint(ColumnNames={"key","userId"})) : key, userId 복합 유니크
@Table(
    name="idempotency_Keys",
    uniqueConstraints=@UniqueConstraint(
        name="uk_idempotency_keys_key_user",
        columnNames={"user_id", "idempotency_key"}
    )
)
public class IdempotencyKey extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    // Key Column : unique=true -> 서비스 전체에서 이 문자열은 한 번만 사용
    @Column(name="idempotency_key", nullable=false, length=64) // key length() 64
    private String idempotencyKey;
    @Column(nullable=false, name="user_id")
    private Long userId;
    @Column(nullable=false, name="schedule_id")
    private Long scheduleId;

    // 생성자
    @Builder(access=AccessLevel.PRIVATE)
    private IdempotencyKey(String idempotencyKey, Long userId, Long scheduleId){
        this.idempotencyKey=idempotencyKey;
        this.userId=userId;
        this.scheduleId=scheduleId;
    }
    // 정적 팩토리 메서드
    public static IdempotencyKey of(String key, Long userId, Long scheduleId) {
        return new IdempotencyKey(key, userId, scheduleId);
    }
}
