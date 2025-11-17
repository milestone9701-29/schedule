package com.tr.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.dto.auth.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Test 끝나면 롤백
public class AuthAndUserIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // SignUp, Login -> token 받아오는 helper
    private String signUpAndLoginDefaultUser() throws Exception{
        // SignUp
        SignupRequest signupRequest = new SignupRequest(
            "test@example.com",
            "tester",
            "password123" // SignupRequest에 맞게.
        );

        mockMvc.perform(post("/api/auth/signup") // RequestBuilder
            .contentType(MediaType.APPLICATION_JSON) // JACKSON 형식
            .content(objectMapper.writeValueAsString(signupRequest))) // String으로.
            .andExpect(status().isCreated());

            // Login req 내용
        LoginRequest loginRequest = new LoginRequest(
            "test@example.com",
            "password123"
        );

            // 파싱
        String loginResponseBody = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists()) // AuthResponse 기준.
            .andReturn()
            .getResponse()
            .getContentAsString(); // String 형태로.

        JsonNode root = objectMapper.readTree(loginResponseBody);
        return root.get("token").asText(); // Bearer 앞에 붙일 문자열.
    }

    // --
    // login success
    @Test
    void login_success_returnsTokenAndUserInfo() throws Exception{
        // given
        SignupRequest signupRequest = new SignupRequest(
            "login-success@example.com",
            "login-user",
            "pass1234"
        );

        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
            "login-success@example.com",
            "pass1234"
        );

        // when and then
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.user.email").value("login-success@example.com"));
    }

    // login failed
    @Test
    void login_withWrongPassword_returnsError() throws Exception{
        // given : 정상 회원 가입
        SignupRequest signupRequest = new SignupRequest(
            "login-fail@example.com",
            "login-fail",
            "pass1234"
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
            "login-fail@example.com",
            "wrong-pass"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            // 네 GlobalExceptionHandler 매핑에 맞게 401 혹은 403으로 수정
            .andExpect(status().isForbidden())  // or isForbidden()
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").isNotEmpty());
    }


    // Profile - Valid
    @Test
    void getMyProfile_withValidToken_returnsMyInfo() throws Exception{
            // given : 기본 유저 회원가입 + 로그인해서 토큰 확보
            String token = signUpAndLoginDefaultUser();

            // when & then
            mockMvc.perform(get("/api/users/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("tester"));
            // UserProfileResponse 필드명에 맞게 수정

    }

    // Profile - unauthorized
    @Test
    void getMyProfile_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized()); // Security 설정에 따라 401/403 맞춰 수정
    }
}
