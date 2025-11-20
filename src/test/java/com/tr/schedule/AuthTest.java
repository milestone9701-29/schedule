package com.tr.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.tr.schedule.dto.auth.AuthResponse;
import com.tr.schedule.dto.auth.LoginRequest;
import com.tr.schedule.dto.auth.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/*
Test 조건
1. @Test
2. public 아니어도 된다 : 단, JUnit 5부터.
3. 반환 타입 : void
4. throws Exception

@Test
void login_withValidCredentials_returnsToken() throws Exception {
    // ---- //
}

준비(given) -> 실행(when) -> 검증(then) : _로 반드시 분리하여 테스트 메서드 설계할 것.

조건
행위
리턴

*/


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Test 끝나면 롤백
public class AuthTest {

    // 1). 기본 값 상수화
    private static final String DEFAULT_EMAIL="testplayer@alwayssleepy.kr";
    private static final String DEFAULT_USERNAME="test_player";
    private static final String DEFAULT_PASSWORD="password00";

    // 2). util
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public AuthTest(MockMvc mockMvc, ObjectMapper objectMapper){
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    // 3). 회원가입 : email, username, password(request)
    private void signUp(String email,
                        String username,
                        String password) throws Exception{
        SignupRequest signupRequest = new SignupRequest(
            email,
            username,
            password
        );
        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated()); // 201
    }

    // 4). 로그인 : email, Password(request) -> Login + token(String)
    private String loginAndGetToken(String email, String password) throws Exception{
        LoginRequest loginRequest=new LoginRequest(
            email,
            password
        );

        String responseBody=mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode root=objectMapper.readTree(responseBody);
        return root.get("token").asText();
        /* AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        return authResponse.token();*/

    }

    // 5). 편의 메서드 : 가입 - 로그인
    private String signUpAndLoginDefaultUser()throws Exception{
        signUp(DEFAULT_EMAIL,DEFAULT_USERNAME,DEFAULT_PASSWORD);
        return loginAndGetToken(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    // 6). 회원가입 : 201 : Response 값에 맞출 것.
    @Test
    void signUp_WithValidRequest_returnsCreated() throws Exception{
        SignupRequest signupRequest=new SignupRequest(
            DEFAULT_EMAIL,
            DEFAULT_USERNAME,
            DEFAULT_PASSWORD
        );
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated()) // 201
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    // 7). 로그인 : 200
    @Test
    void login_WithValidCredentials_returnsToken() throws Exception{
        signUp(DEFAULT_EMAIL,DEFAULT_USERNAME,DEFAULT_PASSWORD);
        LoginRequest loginRequest=new LoginRequest(
            DEFAULT_EMAIL,
            DEFAULT_PASSWORD
        );
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk()) // 200
            .andExpect(jsonPath("$.token").exists());
    }

    // ----------------------------------------------------------------------------------- //

    @Test
    void signUp_withDuplicateEmail_returns409() throws Exception{
        // given : 회원 가입.
        signUp(DEFAULT_EMAIL,DEFAULT_USERNAME,DEFAULT_PASSWORD);
        // when : 같은 이메일로 재요청
        SignupRequest duplicate=new SignupRequest(
            DEFAULT_EMAIL,
            "flying_cat",
            DEFAULT_PASSWORD
        );
        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(duplicate)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.errorCode").value("A409-01"));

        // .andExpect(jsonPath("$.code").value("A409-01"));

    }
}

