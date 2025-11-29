package com.tr.schedule.global.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


// Stack Trace : 예외 발생 과정에서 호출된 메서드들의 순서와 위치 정보를 나타내는 것.

@Slf4j // logger
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1). Business 예외.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode=ex.getErrorCode();
        ErrorResponse body=ErrorResponse.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    // OptimisticLockException.class, ObjectOptimisticLockingFailureException.class
    // 2025-11-18 11:04
    // JPA/SpringData 예외
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLock(HttpServletRequest request){
        ErrorCode errorCode=ErrorCode.VERSION_CONFLICT;
        ErrorResponse body=ErrorResponse.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }


    // 3). 검증 예외 : Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        // ex.getBindingResult().toString(), Stack Trace 없음.
        log.debug("[VALIDATION] {} - {}", request.getRequestURI(), ex.getBindingResult());// logging
        ErrorResponse body=ErrorResponse.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    // 4). IllegalArgumentException : BAD_REQUEST로 통일.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        // ex.getMessage(), Stack Trace 없음.
        log.debug("[BAD_REQUEST] {} - {}", request.getRequestURI(), ex.getMessage()); // logging
        ErrorResponse body=ErrorResponse.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    // 5). NoResourceFoundException : 404
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request){
        ErrorCode errorCode=ErrorCode.NOT_FOUND;
        log.debug("[NOT_FOUND] {} - {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse body=ErrorResponse.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    // 6). Exception : 500 : 진짜 서버 버그라서 스택까지 남기는 것.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

        // 두 번째 인자가 Throwable임. -> ex 그대로 던지기.
        log.error("[INTERNAL_ERROR] {} - {}",
            request.getRequestURI(),
            ex.getMessage(),
            ex); // 마지막 인자 Throwable -> Stack Trace까지.
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        ErrorResponse body=ErrorResponse.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }
}
/*
*  ex.getBindingResult().getFieldErrors().forEach(err ->
        log.warn("[VALIDATION] {} {} field={} value={} message={}",
                request.getMethod(),
                request.getRequestURI(),
                err.getField(),
                err.getRejectedValue(),
                err.getDefaultMessage())
    );*/
