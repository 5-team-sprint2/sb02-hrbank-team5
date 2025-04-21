package com.hrbank.dto.department;

import java.time.LocalDate;
import java.util.UUID;

public record DepartmentDto(
        UUID id,
        String name,
        String description,
        LocalDate establishedDate
) {}
