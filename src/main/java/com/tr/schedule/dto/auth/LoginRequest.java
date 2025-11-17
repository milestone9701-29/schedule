package com.tr.schedule.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


// 로그인 필요 입력 요구사항 : email, password.
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class LoginRequest {

    @NotBlank @Email @Size(max=100)
    private String email;

    @NotBlank @Size(min=8, max=100)
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
