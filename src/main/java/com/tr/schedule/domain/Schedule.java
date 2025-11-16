package com.tr.schedule.domain;

// @Version : Entity 수정 시 필드 값 자동 증가. : cnt++와 같이 우선순위 잡는데 쓰는 모양.
import com.tr.schedule.dto.schedule.ScheduleUpdateRequest;
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
    @Builder
    public Schedule(User owner, String title, String content){
        this.owner = owner;
        this.title = title;
        this.content = content;
    }
    public void scheduleUpdateFrom(ScheduleUpdateRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
    }
}
