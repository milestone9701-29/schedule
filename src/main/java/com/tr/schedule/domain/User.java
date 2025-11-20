package com.tr.schedule.domain;

// class -> class : extends

// DB : Entity 생성, Table 생성. uniqueKey
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// id, username, email, pwhash, version
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="users", // DDL : Index 정의
    uniqueConstraints=@UniqueConstraint(name = "uk_users_email", columnNames="email"),
    indexes={@Index(name="idx_users_username", columnList="username")}) // index name, columnname : Entity
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=30)
    private String username;
    @Column(name="email", nullable=false, unique=true, length=100)
    private String email;
    @Column(name="password_hash", nullable=false, length=100)
    private String passwordHash;

    // roles : 권한 부여
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(
        name="user_roles",
        joinColumns=@JoinColumn(name="user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name="role",nullable=false,length=20)
    private Set<Role> roles=new HashSet<>();

    @Version
    private Long version;

    // 직접 작성 : username, email, pwHash
    @Builder
    public User(String username, String email, String passwordHash){
        this.username=username;
        this.email=email;
        this.passwordHash=passwordHash;
    }

    // 추가 : 편의용
    public void addRole(Role role){
        roles.add(role);
    }
    public Set<Role> getRoles(){ // 불변 Set
        return Collections.unmodifiableSet(roles);
    }
}
