package com.hrbank.service;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;

public interface EmployeeService {
  CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition);
}
