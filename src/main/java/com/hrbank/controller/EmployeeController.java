package com.hrbank.controller;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.hrbank.dto.employee.EmployeeCreateRequest;
import com.hrbank.dto.employee.EmployeeDistributionDto;
import com.hrbank.dto.employee.EmployeeDto;
import com.hrbank.dto.employee.EmployeeSearchCondition;
import com.hrbank.dto.employee.EmployeeTrendDto;
import com.hrbank.dto.employee.EmployeeUpdateRequest;
import com.hrbank.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "직원 관리", description = "직원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @Operation(summary = "직원 목록 조회", description = "직원 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CursorPageResponseEmployeeDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INVALID_CURSOR\", \"status\": 400, \"message\": \"커서 값이 올바르지 않습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto> searchEmployees(@ModelAttribute EmployeeSearchCondition condition) {
    return ResponseEntity.ok(employeeService.searchEmployees(condition));
  }


  @Operation(summary = "직원 수정", description = "직원 정보를 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"EMAIL_ALREADY_EXISTS\", \"status\": 400, \"message\": \"이미 사용 중인 이메일입니다.\"}"))),
      @ApiResponse(responseCode = "404", description = "직원 또는 부서를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"EMPLOYEE_NOT_FOUND\", \"status\": 404, \"message\": \"직원을 찾을 수 없습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @PatchMapping("/{id}")
  public ResponseEntity<EmployeeDto> updateEmployee(
      @PathVariable Long id,
      @RequestPart("employee")  EmployeeUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile file,
      HttpServletRequest httpRequest
  ) {
    String ip = httpRequest.getRemoteAddr();
    BinaryContentCreateRequest fileRequest = BinaryContentCreateRequest.of(file);
    EmployeeDto updated = employeeService.update(id, request, fileRequest, ip);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "직원 등록", description = "새로운 직원을 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"EMAIL_ALREADY_EXISTS\", \"status\": 400, \"message\": \"이미 사용 중인 이메일입니다.\"}"))),
      @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"DEPARTMENT_NOT_FOUND\", \"status\": 404, \"message\": \"부서를 찾을 수 없습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @PostMapping
  public ResponseEntity<EmployeeDto> createEmployee(
      @Parameter(description = "직원 등록 요청")
      @RequestPart("employee") @Valid EmployeeCreateRequest request,

      @Parameter(description = "프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profileImage, HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    return ResponseEntity.ok(employeeService.create(request, profileImage, ip));
  }

  @Operation(summary = "직원 삭제", description = "직원을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "직원을 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"EMPLOYEE_NOT_FOUND\", \"status\": 404, \"message\": \"직원을 찾을 수 없습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@Parameter(description = "직원 ID") @PathVariable Long id, HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    employeeService.delete(id, ip);
    return ResponseEntity.noContent().build();
  }


  @Operation(summary = "직원 상세 조회", description = "직원 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDto.class))),
      @ApiResponse(responseCode = "404", description = "직원을 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"EMPLOYEE_NOT_FOUND\", \"status\": 404, \"message\": \"직원을 찾을 수 없습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> getEmployeeDetails(@Parameter(description = "직원 ID") @PathVariable Long id) {
    EmployeeDto employeeDto = employeeService.findById(id);
    return ResponseEntity.ok(employeeDto);
  }


  @Operation(summary = "직원 수 조회", description = "지정된 조건에 맞는 직원 수를 조회합니다. 상태 필터링 및 입사일 기간 필터링이 가능합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INVALID_DATE_RANGE\", \"status\": 400, \"message\": \"시작일은 종료일보다 이후일 수 없습니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @GetMapping("/count")
  public long getEmployeeCount(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate toDate) {

    return employeeService.getEmployeeCount(status, fromDate, toDate);
  }

  @Operation(summary = "직원 수 추이 조회", description = "지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회합니다. 파라미터를 제공하지 않으면 최근 12개월 데이터를 월 단위로 변환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 시간 단위", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INVALID_UNIT\", \"status\": 400, \"message\": \"지원하지 않는 단위입니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @GetMapping("/stats/trend")
  public ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrends(
      @RequestParam(value = "from", required = false) LocalDate from,
      @RequestParam(value = "to", required = false) LocalDate to,
      @RequestParam(value = "unit", defaultValue = "month") String unit
      ) {
    return ResponseEntity.ok(employeeService.findEmployeeTrends(from, to, unit));
  }

  @Operation(summary = "직원 분포 조회", description = "지정된 기준으로 그룹화된 직원 분포를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 그룹화 기준", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INVALID_GROUP_BY\", \"status\": 400, \"message\": \"지원하지 않는 그룹화 기준입니다.\"}"))),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"code\": \"INTERNAL_ERROR\", \"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
  })
  @GetMapping("/stats/distribution")
  public List<EmployeeDistributionDto> getEmployeeDistribution(
          @RequestParam(defaultValue = "department") String groupBy,
          @RequestParam(defaultValue = "ACTIVE") String status
  ) {
    return employeeService.getEmployeeDistribution(groupBy, status);
  }
}
