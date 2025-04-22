package com.hrbank.mapper;

import com.hrbank.dto.department.DepartmentDto;
import com.hrbank.entity.Department;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    DepartmentDto toDto(Department department);
    List<DepartmentDto> toDtoList(List<Department> departments);
}
