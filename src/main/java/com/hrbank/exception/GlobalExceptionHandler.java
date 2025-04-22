package com.hrbank.exception;

import com.hrbank.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RestException.class)
  public ResponseEntity<ErrorResponse> handleRestException(RestException e) {
    return ResponseEntity.status(e.getStatus())
        .body(new ErrorResponse(e.getMessage()));
  }

  // 필요 시 다른 예외 처리도 추가 가능
}