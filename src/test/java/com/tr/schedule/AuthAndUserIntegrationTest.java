package com.tr.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.schedule.domain.User;
import com.tr.schedule.dto.auth.SignupRequest;
import com.tr.schedule.dto.auth.LoginRequest;
import com.tr.schedule.dto.schedule.ScheduleCreateRequest;
import com.tr.schedule.repository.ScheduleRepository;
import com.tr.schedule.repository.UserRepository;
import com.tr.schedule.service.ScheduleService;
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
    UserRepository userRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ObjectMapper objectMapper;

    // SignUp, Login -> token 받아오는 helper
    private String signUpAndLoginDefaultUser() throws Exception{
        // SignUp
        SignupRequest signupRequest = new SignupRequest(
            "testplayer00@alwayssleepy.kr",
            "test_player",
            "password00"
        );

        // post
        // JACKSON
        // Parsing
        // status
        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated()); // 201

        // Login
        LoginRequest loginRequest = new LoginRequest(
            "testplayer00@alwayssleepy.kr",
            "password00"
        );

        // Parsing : String
        String loginResponseBody = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk()) // 200
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
        // SignUp
        SignupRequest signupRequest = new SignupRequest(
            "testplayer00@alwayssleepy.kr",
            "test_player",
            "password00"
        );

        // post
        // JACKSON
        // objectMapper.writeValueAsString(req) : 문자열로 파싱
        // status
        mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated()); // 201


        LoginRequest loginRequest = new LoginRequest(
            "testplayer00@alwayssleepy.kr",
            "password00"
        );

        // post
        // JACKSON
        // objectMapper.writeValueAsString(req) : 문자열로 파싱
        // status
        // token이 존재하는지?
        // $.user.email : value(예상 기댓값)
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk()) // 200
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.user.email").value("testplayer00@alwayssleepy.kr"));
    }

    // login failed
    @Test
    void login_withWrongPassword_returnsError() throws Exception{
        // 정상적인 회원가입
        SignupRequest signupRequest = new SignupRequest(
            "login-fail@example.com",
            "login-fail",
            "pass1111"
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated()); // 201

        // 로그인 실패
        LoginRequest loginRequest = new LoginRequest(
            "login-fail@example.com",
            "wrong-pass9900"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized()) // 401
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.message").isNotEmpty());
    }


    // Profile - Valid
    @Test
    void getMyProfile_withValidToken_returnsMyInfo() throws Exception {
        // given: 회원 하나 만들고, 로그인해서 JWT 토큰 뽑기
        User user = userRepository.save(User.builder()
            .username("testuser")
            .email("testplayer00@alwayssleepy.kr")
            .password(passwordEncoder.encode("password1234!"))
            .build()
        );

        LoginRequest loginRequest = new LoginRequest(
            user.getEmail(),
            "password1234!"
        );

        // 로그인 요청 → 응답 JSON에서 token 추출
        String loginResponseBody = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode root = objectMapper.readTree(loginResponseBody);
        String accessToken = root.get("token").asText();

        // when & then: /api/users/me 호출 → UserProfileResponse 구조 검증
        mockMvc.perform(get("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isOk())
            // UserMapper.toUserProfileResponse(User user) 기준 검증
            .andExpect(jsonPath("$.id").value(user.getId()))
            .andExpect(jsonPath("$.username").value(user.getUsername()))
            .andExpect(jsonPath("$.email").value(user.getEmail()))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    // Profile - unauthorized : ErrorResponse
    @Test
    void getMyProfile_withoutToken_returnsUnauthorized() throws Exception {

        String token = signUpAndLoginDefaultUser();

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized()) // 401
                .andExpect(jsonPath("$.code").value(""))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.path").value("/api/users/me"));

    }

    // createSchedule - IsCreated()
    @Test
    void createSchedule_withValidToken_returnsCreatedSchedule() throws Exception{
        // 토큰 들고오기
        String token=signUpAndLoginDefaultUser();
         // DTO
        ScheduleCreateRequest request = new ScheduleCreateRequest(
            "테스트 일정", // title
            "테스트 내용"// content
        );
        //when and then
         mockMvc.perform(post("/api/schedules")
                 .header("Authorization", "Bearer " + token)
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andExpect(status().isCreated()) // 201
             .andExpect(jsonPath("$.title").value("테스트 일정"))
             .andExpect(jsonPath("$.content").value("테스트 내용"));
     }

    @Test
    void createSchedule_withoutToken_returnsUnauthorized() throws Exception {
        ScheduleCreateRequest request = new ScheduleCreateRequest(
            "익명 일정",
            "익명 내용"
        );

        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized()) // 401
            .andExpect(jsonPath("$.code").value("AUTH_INVALID_PASSWORD"))
            .andExpect(jsonPath("$.path").value("/api/schedules"));
    }

    @Test
    void getMySchedules_withValidToken_returnsOnlyMySchedules() throws Exception {
        String token = signUpAndLoginDefaultUser();

        // 같은 유저로 일정 두 개 생성
        createSchedule(token, "제목1");
        createSchedule(token, "제목2");

        mockMvc.perform(get("/api/schedules/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()) // 200
            .andExpect(jsonPath("$.content.length()").value(2));
        // 응답이 List인지, page 래퍼인지에 따라 .andExpect(jsonPath("$.content", hasSize(2))); 등.
    }

    @Test
    void request_withInvalidToken_returnsJwtInvalidError() throws Exception{
        String invalidToken="this.is.not.jwt";

        mockMvc.perform(get("/api/schedules/me")
            .header("Authorization","Bearer " + invalidToken))
            .andExpect(status().isUnauthorized()) // 401
            .andExpect(jsonPath("$.code").value("JWT_401_INVALID"))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void accessAdminEndpoint_withUserRole_returnsForbidden() throws Exception{
        // USER로 회원가입 + 로그인
        String token = signUpAndLoginDefaultUser();
        mockMvc.perform(get("/api/admin/some-endpoint")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden()) // 403
            .andExpect(jsonPath("$.code").value("AUTH_INVALID_PASSWORD"))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }



    private void createSchedule(String token, String title) throws Exception {
        ScheduleCreateRequest request = new ScheduleCreateRequest(
            title,
            "테스트 내용"
        );

        mockMvc.perform(post("/api/schedules")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }


}
// 1. Request Value -> Response
// 2. 이미 싱성한 토큰 갖고 접근하는 경우. : createSchedule
