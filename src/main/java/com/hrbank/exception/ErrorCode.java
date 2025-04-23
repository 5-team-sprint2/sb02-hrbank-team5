package com.hrbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부서를 찾을 수 없습니다."),
  DEPARTMENT_NAME_DUPLICATED(HttpStatus.CONFLICT, "부서 이름이 이미 존재합니다."),
  DEPARTMENT_HAS_EMPLOYEES(HttpStatus.BAD_REQUEST, "소속된 직원이 있는 부서는 삭제할 수 없습니다."),
  PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."),
  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "직원을 찾을 수 없습니다."),
  CHANGE_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 변경 이력이 없습니다."),
  INVALID_CURSOR(HttpStatus.BAD_REQUEST, "커서 값이 올바르지 않습니다."),
  INVALID_CHANGE_LOG_DATA(HttpStatus.BAD_REQUEST, "변경 로그 데이터가 올바르지 않습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}