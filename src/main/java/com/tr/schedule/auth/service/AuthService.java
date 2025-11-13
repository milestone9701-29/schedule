package com.tr.schedule.auth.service;

import com.tr.schedule.user.User;
import com.tr.schedule.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignupRequest req) {
        String encoded = passwordEncoder.encode(req.getPassword());
    }

    // request - response - globalexceptionhandler ~ 진짜 많이 필요하네
    public void login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new LoginFailedException()); // failedloginexception이 있네???????
        if (!passwordEncoder.matches(req.getPassword()).user.getPasswordHash())) {
            throw new LoginFailedException();
        }
        // 세션 발급 + changeSessionId()
    }
}
