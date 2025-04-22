package com.hrbank.dto.department;

import java.util.List;

public record CursorPageResponseDepartmentDto(
        List<DepartmentDto> content,
        String nextCursor,
        Long nextIdAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {}

