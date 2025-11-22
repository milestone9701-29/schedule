package com.tr.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.tr.schedule.dto.auth.AuthTokens;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.auth.LoginRequest;
import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.global.security.JwtTokenProvider;
import com.tr.schedule.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserRepository userRepository;

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
        /* AuthTokens authResponse = objectMapper.readValue(responseBody, AuthTokens.class);
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

    // 8). 중복 이메일로 회원가입 신청 : 409
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
            .andExpect(jsonPath("$.code").value("A409-01"));

        // .andExpect(jsonPath("$.code").value("A409-01"));
    }

    // 9). 비밀번호를 틀리는 케이스 : 401 : A401-01
    @Test
    void login_withWrongPassword_returns401() throws Exception{
        // given : 회원가입
        signUp(DEFAULT_EMAIL,DEFAULT_USERNAME,DEFAULT_PASSWORD);
        // when : 패스워드를 틀린 경우.
        LoginRequest loginRequest=new LoginRequest(
            DEFAULT_EMAIL,
            "wrong-password"
        );

        // then
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized()) // 401
            .andExpect(jsonPath("$.code").value("A401-01"));
    }

    // 10). 없는 이메일로 로그인 : 401
    @Test
    void login_withWrongEmail_returns401() throws Exception{
        signUp(DEFAULT_EMAIL,DEFAULT_USERNAME,DEFAULT_PASSWORD);
        // when : 없는 이메일로 로그인
        LoginRequest loginRequest=new LoginRequest(
            DEFAULT_EMAIL,
            DEFAULT_PASSWORD
        );

        // then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized()) // 401
            .andExpect(jsonPath("$.code").value("A401-01"));
    }

    // ----------------------------------------------------------------------------------- //
    // 보호된 엔드포인트 + 토큰 실험

    // 11). 토큰 없이 접근 : 401
    @Test
    void getMyProfile_withOutToken_returns401() throws Exception{
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("A401-02"));

    }

    // 12). 쓰레기 토큰 : 401
    @Test
    void getMyProfile_withGarbageToken_returns401() throws Exception{
        mockMvc.perform(get("/api/users/me")
            .header("Authorization", "Bearer but-"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("A401-03"));
    }

    // 13). 정상 토큰 : 200 + UserProfiles
    @Test
    void getMyProfile_withValidToken_returnsMyInfo() throws Exception{
        String token=signUpAndLoginDefaultUser();

        mockMvc.perform(get("/api/users/me")
            .header("Authorization", "Bearer "+token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME));
    }

    // 14). 회원가입 Validation 실패 : 400
    @Test
    void signUp_withInvalidEmailFormat_returns400() throws Exception{
        SignupRequest signupRequest=new SignupRequest(
            "not-an-email",
            DEFAULT_USERNAME,
            DEFAULT_PASSWORD
        );

        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isBadRequest());
    }

    // 15). 만료 토큰 : 401 : JwtTokenProvider.generateExpiredToken(userId);
    @Test
    void getMyProfile_withExpiredToken_returns401() throws Exception{
        // given : userId and expired token
        signUp(DEFAULT_EMAIL,DEFAULT_USERNAME,DEFAULT_PASSWORD);
        User user=userRepository.findByEmail(DEFAULT_EMAIL).orElseThrow();
        String expiredToken=jwtTokenProvider.generateExpiredToken(user.getId());

        // when and then
        mockMvc.perform(get("/api/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer "+expiredToken))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("A401-03"));
    }

    // 16). Authorization : ADMIN - USER 200 403
    @Test
    void adminEndpoint_withUserRole_returns403() throws Exception{
        // given : userToken
        String userToken=signUpAndLoginDefaultUser(); // USER
        mockMvc.perform(get("/api/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer "+userToken))
            .andExpect(status().isForbidden());
    }
}

