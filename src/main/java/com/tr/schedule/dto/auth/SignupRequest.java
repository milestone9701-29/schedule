package com.tr.schedule.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수정 필요
// 가입 시 필요 입력 요구사항 : username, email, password
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED) // JPA JSON -> parameter 비어있는 생성자 생성
public class SignupRequest {

    @NotBlank @Email @Size(max=100)
    private String email;

    @NotBlank @Size(max=30)
    private String username;

    @NotBlank @Size(min=8, max=100)
    private String password;

    public SignupRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
