package com.hrbank.repository;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;

public interface EmployeeRepositoryCustom {
  CursorPageResponseEmployeeDto findAllWithFilter(EmployeeSearchCondition condition);
}
