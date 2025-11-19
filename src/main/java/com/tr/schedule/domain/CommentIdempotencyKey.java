package com.tr.schedule.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name="comment_idempotency_keys",
    uniqueConstraints=@UniqueConstraint(name="uk_comment_idempotency_key",
    columnNames={"key","user_id","schedule_id"}
    )
)
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class CommentIdempotencyKey extends BaseTimeEntity{
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=64) // key length() 64
    private String key;
    @Column(name="user_id", nullable=false)
    private Long userId;
    @Column(name="schedule_id", nullable=false)
    private Long scheduleId;
    @Column(name="comment_id", nullable=false)
    private Long commentId;

    // 생성자
    @Builder(access=AccessLevel.PRIVATE)
    private CommentIdempotencyKey(String key, Long userId, Long scheduleId, Long commentId){
        this.key=key;
        this.userId=userId;
        this.scheduleId=scheduleId;
        this.commentId=commentId;
    }

    public static CommentIdempotencyKey of(String key, Long userId, Long scheduleId, Long commentId){
        return new CommentIdempotencyKey(key, userId, scheduleId, commentId);
    }
}
