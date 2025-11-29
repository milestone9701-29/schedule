package com.tr.schedule.global.security;

import com.tr.schedule.domain.User;
import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.BusinessException;
import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 로그인 -> 이메일로 조회
    // Security Filter -> AuthenticationManager -> DaoAuthenticationProvider : 계층 구분 하시라고.
    // AuthenticationEntryPoint / AccessDeniedHandler
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user=findByEmailOrThrow(email);
        return new CustomUserDetails(user);
    }


    // JWT 안의 id로 조회하는 헬퍼.
    public UserDetails loadUserById(Long id) {
        User user = findByIdOrThrow(id); // 시큐리티 전용 예외
        checkBanned(user);
        return new CustomUserDetails(user);
    }

    private User findByEmailOrThrow(String email){
        return userRepository.findByEmail(email)
            .orElseThrow(()-> new UsernameNotFoundException("Invalid credentials" + email)); // 시큐리티 전용 예외
    }
    private User findByIdOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(()->new UsernameNotFoundException("Invalid credentials" + userId));
    }
    private void checkBanned(User user){
        if(user.isBanned()){
            throw new BusinessAccessDeniedException(ErrorCode.USER_BANNED); // 403
        }
    }
}

/*
Authentication 실패
loadUserByUsername(email) -> UserDetails
-> 1. PW 비교 실패 -> BadCredentialsException
-> 2. Cannot find user : UsernameNotFoundException
--
REST : HttpStatus.UNAUTHORIZED (401) + 에러 메시지
FORM LOGIN : /login?error 로 redirect
 */

/*
loadUserById(id) -> UserDetails
UsernameNotFoundException : token이 있으나 가리키는 유저가 없는 경우 : 회원 탈퇴 등.
-> 1. 401 처리
-> 2. filter에서 catch -> SecurityContextHolder.clearContext() -> AuthenticationEntryPoint 호출.
 */
