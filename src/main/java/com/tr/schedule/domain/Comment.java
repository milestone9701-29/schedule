package com.tr.schedule.domain;



import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.VersionErrorException;
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

    // 어떠한 일정의 댓글인지?
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="schedule_id", nullable=false)
    private Schedule schedule;

    // author
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="author_id", nullable=false)
    private User author;
    @Column(nullable=false, length=100)
    private String content;

    @Version
    private Long version;

    // 직접 작성 : schedule, author, content
    @Builder(access=AccessLevel.PRIVATE)
    private Comment(Schedule schedule, User author, String content) {
        this.schedule=schedule;
        this.author=author;
        this.content=content;
    }

    public static Comment of(Schedule schedule, User author, String content){
        return Comment.builder()
            .schedule(schedule)
            .author(author)
            .content(content)
            .build();
    }

    // 내용 수정.
    public void update(String content, Long expectedVersion){
        if(!this.version.equals(expectedVersion)){ // version 검사.
            throw new VersionErrorException(ErrorCode.SCHEDULE_VERSION_CONFLICT);
        }
        this.content = content;
    }
}
