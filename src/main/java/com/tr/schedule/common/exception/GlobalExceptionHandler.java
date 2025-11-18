package com.tr.schedule.common.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j // logger
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
            errorCode.getCode(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // OptimisticLockException.class, ObjectOptimisticLockingFailureException.class
    // 2025-11-18 11:04
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLock(Exception ex, HttpServletRequest request){
        // 일정, 댓글 : 삼항 조건 연산자로 Schedule - Comment 가르기
        ErrorCode errorCode=request.getRequestURI().contains("/comments") //
            ? ErrorCode.SCHEDULE_VERSION_CONFLICT
            : ErrorCode.COMMENT_VERSION_CONFLICT;

        ErrorResponse errorResponse=new ErrorResponse(
            errorCode.getCode(),
            errorCode.getDefaultMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse errorResponse=new ErrorResponse(errorCode.getCode(), message, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getDefaultMessage(), request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }
}
