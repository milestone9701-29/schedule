package com.tr.schedule.global.security;

import com.tr.schedule.global.exception.BusinessAccessDeniedException;
import com.tr.schedule.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    // 2025-11-24 : 어노테이션 검증 : Controller Method의 Parameter가 CurrentUser인지 체크.
    // 1. @Override public boolean supportParameter(MethodParameter parameter) : 검증
    // 1). parameter.hasParameterAnnotation(AuthUser.class) : 해당 parameter가 AuthUser의 Annotation을 갖고 있는지?
    // 2). 해당 Parameter와 CurrentUser의 Parameter Type이 일치하는지?
    @Override
    public boolean supportsParameter(MethodParameter parameter){
        // 여러 Resolver가 있다 가정했을 때의 충돌 방지.
        return parameter.hasParameterAnnotation(AuthUser.class)
            &&parameter.getParameterType().equals(CurrentUser.class);
    }

    // 1. public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
    // 1). Client가 /api/users/me 같은 endpoint 호출
    // 2). filterChain 통과(JwtAuthenticationFilter) : 토큰 파싱
    // 3). SecurityContextHolder.getContext.getAuthentication(authentication);
    // 4). DispatcherServlet -> 해당 컨트롤러, 메서드를 찾음.
    // 5). Controller Method에 있는 Parameter를 보고,
    // 6). HandlerMethodArgumentResolver 목록 순회
    // 7). supportsParameter() true인 resolver 찾음 -> resolverArgument() 호출 -> 실제 Parameter 값을 넣음.
    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication(); // 이 요청을 보낸 유저의 인증 정보 : Authentication.

        // 1. 경우의 수
        // 1). auth==null : 필터에서 인증을 안했거나, token이 없거나, 유효하지 않아서 SecurityContext에 아무 것도 안들어간 상황. : 로그인이 되지 않은 상황.
        // 2). !(auth.getPrincipal() instanceof CustomUserDetails principal) : 다른 type의 Authentication 또는 익명 사용자(AnonymousAuthenticationToken)
        // -> JWT를 통해 인증이 왼료된 유저가 아님.
        if(auth==null||!(auth.getPrincipal() instanceof CustomUserDetails principal)){
            throw new BusinessAccessDeniedException(ErrorCode.AUTH_TOKEN_MISSING);
        }

        return CurrentUser.from(principal); // Controller에서 쓰기 좋게끔 DTO로 변환.
    }
}
