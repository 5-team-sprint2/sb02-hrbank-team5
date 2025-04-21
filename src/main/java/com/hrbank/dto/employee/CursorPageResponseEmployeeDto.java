package com.hrbank.dto.employee;

import java.util.List;
import java.util.UUID;

public record CursorPageResponseEmployeeDto(
    List<EmployeeDto> content,
    UUID nextCursorId,
    boolean hasNext
) {}