package com.tr.schedule.global.security;

import com.tr.schedule.domain.User;
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

    //로그인 -> 이메일로 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Cannot find userId : " + email ));
        return new CustomUserDetails(user);
    }
    // JWT 안의 id로 조회하는 헬퍼.
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new UsernameNotFoundException("Cannot find userId :  " + id));
        return new CustomUserDetails(user);
    }
}
