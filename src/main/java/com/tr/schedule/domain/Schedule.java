package com.tr.schedule.domain;

// @Version : Entity 수정 시 필드 값 자동 증가. : cnt++와 같이 우선순위 잡는데 쓰는 모양.

import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.VersionErrorException;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* WHERE user_id=?ORDER BY updated_at DESC */
// id title content owner(User) version
// Schedule : owner(일정 소유자) Comment : author(작성자)

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Proxy 생성 : protected Schedule() {}
@Entity
@Table(name="schedules", indexes={@Index(name="idx_schedule_owner_updated", columnList="owner_id, updated_at")})
public class Schedule extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) // N
    @JoinColumn(name="owner_id", nullable=false)
    private User owner;
    @Column(nullable=false, length=50)
    private String title;
    @Column(nullable=false, length=200)
    private String content;

    @Version
    private Long version;

    // 직접 작성 : owner, title, content
    // 도메인 생성 전용. : 외부 Access 접근 제어 : PRIVATE.
    @Builder(access=AccessLevel.PRIVATE)
    private Schedule(User owner, String title, String content){
        this.owner = owner;
        this.title = title;
        this.content = content;
    }

    // 정적 팩토리 메서드
    public static Schedule of(User owner, String title, String content){
        return Schedule.builder()
            .owner(owner)
            .title(title)
            .content(content)
            .build();
    }

    public void update(String title, String content, Long expectedVersion){
        if(!this.version.equals(expectedVersion)){
            throw new VersionErrorException(ErrorCode.SCHEDULE_VERSION_CONFLICT);
        }
        this.title = title;
        this.content = content;
    }
}
