package com.tr.schedule.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("ACCESS_DENIED", ex.getMessage()));
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("PASSWORD_MISMATCH", ex.getMessage()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCommentLimitExceeded(CommentLimitExceededException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("CONFLICT", ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage()));
    }
}
