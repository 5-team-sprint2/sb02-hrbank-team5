package com.hrbank.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "에러 응답")
public record ErrorResponse(
        @Schema(description = "에러 발생 시각")
        String timestamp,
        @Schema(description = "HTTP 상태 코드")
        int status,
        @Schema(description = "에러 메세지")
        String message,
        @Schema(description = "상세 설명")
        String details
) {
    public static ErrorResponse of(int status, String message, String details, String timestamp) {
        return new ErrorResponse(timestamp, status, message, details);
    }
}
