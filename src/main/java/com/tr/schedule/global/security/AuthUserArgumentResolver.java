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

    // 1. @Override public boolean supportParameter(MethodParameter parameter) : 검증
    // 1). parameter.hasParameterAnnotation(AuthUser.class) : 해당 parameter가 AuthUser의 Annotation을 갖고 있는지?
    // 2). 해당 Parameter와 CurrentUser의 Parameter Type이 일치하는지?
    @Override
    public boolean supportsParameter(MethodParameter parameter){
        return parameter.hasParameterAnnotation(AuthUser.class)
            &&parameter.getParameterType().equals(CurrentUser.class);
    }

    // 1. public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
    // 1).
    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!(auth.getPrincipal() instanceof CustomUserDetails principal)){
            throw new BusinessAccessDeniedException(ErrorCode.AUTH_TOKEN_MISSING);
        }

        return CurrentUser.from(principal);
    }
}
