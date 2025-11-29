package com.tr.schedule.domain;

// class -> class : extends

// DB : Entity 생성, Table 생성. uniqueKey
import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;

/*@SQLDelete(sql="UPDATE users SET is_delete = 'Y' WHERE user_id = ?")
@FilterDef(name="notDeletedUserFilter")
@Filter(name="notDeletedUserFilter", condition="is_delete ='N'")*/

// id, username, email, pwhash, version
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="users", // DDL : Index 정의
    uniqueConstraints=@UniqueConstraint(name = "uk_users_email", columnNames="email"),
        indexes={@Index(name="idx_users_username", columnList="username")}) // index name, column name : Entity
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=30)
    private String username;
    @Column(name="email", nullable=false, length=100)
    private String email;
    @Column(name="password_hash", nullable=false, length=100)
    private String passwordHash;
    @Column(length=254)
    private String profileImageUrl;
    @Column(length=200)
    private String bio;
    @Column(nullable=false)
    private boolean banned; // default=false;

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
    private User(String username, String email, String passwordHash){
        this.username=username;
        this.email=email;
        this.passwordHash=passwordHash;
        this.roles.add(Role.USER); // default
    }

    public void changeEmail(String email){
        this.email=email;
    }
    public void changePassword(String password){
        this.passwordHash=password;
    }
    public void changeProfile(String username, String profileImageUrl, String bio){
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
    }

    public void ban(){ this.banned=true; }
    public void unBan(){ this.banned=false; }
    public boolean isBanned(){ return banned;}

    public void addRole(Role role){
        roles.add(role);
    }
    public void removeRole(Role role){
        if(role.equals(Role.USER)){ throw new BusinessAccessDeniedException(ErrorCode.USER_FORBIDDEN); }
        roles.remove(role);
    }
    public RoleSet roleSet(){ return RoleSet.of(roles); }
}
