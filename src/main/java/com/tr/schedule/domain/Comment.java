package com.tr.schedule.domain;



import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
// id content author
// Schedule : owner(일정 소유자) Comment : author(작성자)


@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name="comments", indexes={@Index(name="idx_comment_schedule_created", columnList="schedule_id, create_at")})
public class Comment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="schedule_id", nullable=false)
    private Schedule schedule;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="author_id", nullable=false)
    private User author;
    @Column(nullable=false, length=100)
    private String content;

    @Version
    private Long version;

    // 직접 작성 : schedule, author, content
    @Builder
    public Comment(User author, Schedule schedule, String content) {
        this.author = author;
        this.schedule = schedule;
        this.content = content;
    }
    public void commentUpdate(String content){
        this.content = content;
    }
}
