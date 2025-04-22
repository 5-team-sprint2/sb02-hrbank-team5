package com.hrbank.service.basic;

import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import com.hrbank.entity.BinaryContent;
import com.hrbank.entity.Department;
import com.hrbank.entity.Employee;
import com.hrbank.mapper.EmployeeMapper;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.repository.DepartmentRepository;
import com.hrbank.repository.EmployeeRepository;
import com.hrbank.service.EmployeeChangeLogService;
import com.hrbank.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final EmployeeMapper employeeMapper;
  private final EmployeeChangeLogService changeLogService;


  @Override
  public CursorPageResponseEmployeeDto searchEmployees(EmployeeSearchCondition condition) {
    return employeeRepository.findAllWithFilter(condition);
  }

  @Override
  public EmployeeDto update(Long id, EmployeeUpdateRequest request, String ip) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RestException(ErrorCode.EMPLOYEE_NOT_FOUND));

    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new RestException(ErrorCode.DEPARTMENT_NOT_FOUND));

    BinaryContent profileImage = null;
    if (request.profileImageId() != null) {
      profileImage = binaryContentRepository.findById(request.profileImageId())
          .orElseThrow(() -> new RestException(ErrorCode.PROFILE_IMAGE_NOT_FOUND));
    }
    // RestException 나중에 머지 되면 바꿀 예정

    // 기존 상태 보존
    Employee before = new Employee(
        employee.getName(), employee.getEmail(), employee.getEmployeeNumber(),
        employee.getDepartment(), employee.getPosition(), employee.getHireDate(),
        employee.getStatus()
    );
    before.changeProfileImage(employee.getProfileImage());

    // 값 변경
    employee.changeDepartment(department);
    employee.changePosition(request.position());
    employee.changeStatus(request.status());
    employee.changeProfileImage(profileImage);
    employee.updateName(request.name());
    employee.updateEmail(request.email());
    employee.updateHireDate(request.hireDate());

    // 이력 로그 저장
    changeLogService.saveChangeLog(before, employee, request.memo(), ip);

    return employeeMapper.toDto(employee);
  }
}