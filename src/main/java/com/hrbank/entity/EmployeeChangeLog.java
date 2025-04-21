package com.hrbank.entity;

import com.hrbank.enums.EmployeeChangeLogType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmployeeChangeLog {

  @Id
  @GeneratedValue
  private Long id;

  // 유형
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeChangeLogType type;

  // 사번
  private String employeeNumber;

  // 메모
  private String memo;

  // IP 주소
  private String ipAddress;

  // 시간
  private LocalDateTime at;

  // change_log_diffs, 변경 상세 정보 저장
  @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EmployeeChangeLogDetail> details = new ArrayList<>();
}