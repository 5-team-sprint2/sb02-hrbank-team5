package com.hrbank.dto.department;

import java.util.List;
import java.util.UUID;

public record CursorPageResponseDepartmentDto(
        List<DepartmentDto> content,
        UUID lastId,
        boolean hasNext
) {}
