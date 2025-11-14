package com.tr.schedule.domain;

// class -> class : extends

// DB : Entity 생성, Table 생성. uniqueKey
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// id, username, email, pwhash, version

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="users",
    uniqueConstraints=@UniqueConstraint(name="uk_users_email", columnNames="email"),
    indexes={@Index(name="idx_users_username", columnList="username")})
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=30)
    private String username;
    @Column(name="email", nullable=false, unique=true, length=100)
    private String email;
    @Column(name="password_hash", nullable=false, length=100)
    private String passwordHash;

    @Version
    private Long version;

    // 직접 작성 : username, email, pwHash
    public User(String username, String email, String passwordHash){
        this.username=username;
        this.email=email;
        this.passwordHash=passwordHash;
    }
}
