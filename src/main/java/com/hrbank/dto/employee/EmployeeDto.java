package com.hrbank.dto.employee;

import com.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeDto(
    UUID id,
    String name,
    String email,
    String employeeNumber,
    String position,
    LocalDate hireDate,
    EmployeeStatus status,
    String departmentName
) {}
