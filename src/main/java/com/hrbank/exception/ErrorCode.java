package com.hrbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부서를 찾을 수 없습니다."),
  PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."),
  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "직원을 찾을 수 없습니다."),

  // BACKUP
  BACKUP_NOT_FOUND(HttpStatus.NOT_FOUND, "백업을 찾을 수 없습니다."),
  BACKUP_LATEST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 상태에 해당하는 백업이 존재하지 않습니다."),
  BACKUP_CSV_NOT_FOUND(HttpStatus.NOT_FOUND, "백업 CSV 파일을 찾을 수 없습니다."),
  BACKUP_ERROR_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "백업 에러 로그 파일을 찾을 수 없습니다."),
  BACKUP_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "이미 진행 중인 백업이 있습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}