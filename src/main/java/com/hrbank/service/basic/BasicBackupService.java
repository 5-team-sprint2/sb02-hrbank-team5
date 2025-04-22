package com.hrbank.service.basic;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.entity.Backup;
import com.hrbank.entity.BinaryContent;
import com.hrbank.entity.Employee;
import com.hrbank.enums.BackupStatus;
import com.hrbank.mapper.BackupMapper;
import com.hrbank.repository.BackupRepository;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.repository.EmployeeChangeLogRepository;
import com.hrbank.repository.EmployeeRepository;
import com.hrbank.service.BackupService;
import jakarta.persistence.EntityNotFoundException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicBackupService implements BackupService {

  private BackupRepository backupRepository;
  private BackupMapper backupMapper;
  private EmployeeRepository employeeRepository;
  private EmployeeChangeLogRepository employeeChangeLogRepository
  private BinaryContentRepository binaryContentRepository;

  @Override
  public void runBackup(String requesterIp) {
    if (!isBackupRequired()) {
      // 백업 필요 없으면 SKIPPED 처리
      Backup skipped = Backup.builder()
          .worker(requesterIp)
          .status(BackupStatus.SKIPPED)
          .startedAt(Instant.now())
          .endedAt(Instant.now())
          .build();
      backupRepository.save(skipped);
      return;
    }

    // 백업 이력 생성
    BackupDto inProgress = createInProgressBackup(requesterIp);

    try {
      // 백업 파일 생성
      // 파일 생성 추후 구현
      Long fileId = generateBackupFile(inProgress.id());

      // 성공 처리
      markBackupCompleted(inProgress.id(), fileId);
    } catch (Exception e) {
      // 에러 로그 파일 저장 (추후 구현)
      Long logFileId = saveErrorLogFile(e); // 가정: 오류 로그 저장 메서드

      // 실패 처리
      markBackupFailed(inProgress.id(), logFileId);
    }
  }

  @Override
  public boolean isBackupRequired() {
    Optional<Backup> lastCompletedBackup = backupRepository.findTopByStatusOrderByEndedAtDesc(
        BackupStatus.COMPLETED);
    if(lastCompletedBackup.isEmpty()){
      return true;
    }

    Instant lastBackupTime = lastCompletedBackup.get().getEndedAt();

    // 특정 시간 이후 직원 업데이트 내역 확인
    // 추후 구현 필요
    return employeeChangeLogRepository.existsByUpdatedAtAfter(lastBackupTime);
  }

  @Override
  public BackupDto createInProgressBackup(String requesterIp) {
    Backup backup = Backup.builder()
        .worker(requesterIp)
        .status(BackupStatus.IN_PROGRESS)
        .startedAt(Instant.now())
        .build();

    Backup saved = backupRepository.save(backup);
    return backupMapper.toDto(saved);
  }

  @Override
  public void markBackupCompleted(Long backupId, Long fileId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new EntityNotFoundException("백업을 찾을 수 없습니다."));

    BinaryContent file = binaryContentRepository.findById(fileId)
        .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다."));

    backup.completeBackup(file);
  }

  @Override
  public void markBackupFailed(Long backupId, Long logFileId) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new EntityNotFoundException("백업을 찾을 수 없습니다."));

    BinaryContent logFile = binaryContentRepository.findById(logFileId)
        .orElseThrow(() -> new EntityNotFoundException("로그 파일을 찾을 수 없습니다."));

    backup.failBackup(logFile);
  }

  private Long generateBackupFile(Long backupId) {
    try {
      File file = createEmployeeCsv();
      return storeAsBinary(file, "text/csv");
    } catch (IOException e) {
      log.error("직원 CSV 파일 생성 실패", e);
      throw new RuntimeException("직원 CSV 파일 생성 중 오류 발생", e);
    }
  }

  private Long saveErrorLogFile(Exception e) {
    try {
      File logFile = createErrorLogFile(e);
      return storeAsBinary(logFile, "text/plain");
    } catch (IOException ioException) {
      log.error("에러 로그 파일 저장 실패", ioException);
      throw new RuntimeException("에러 로그 파일 저장 실패", ioException);
    }
  }

  // 직원 데이터를 CSV로 파일로 저장
  private File createEmployeeCsv() throws IOException {
    List<Employee> employees = employeeRepository.findAll();
    File csvFile = File.createTempFile("employee-backup-", ".csv");

    try (
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "직원번호", "이름", "이메일", "부서", "직급", "입사일", "상태"))
    ) {
      for (Employee employee : employees) {
        csvPrinter.printRecord(
            employee.getId(),
            employee.getEmployeeNumber(),
            employee.getName(),
            employee.getEmail(),
            employee.getDepartment() != null ? employee.getDepartment().getName() : "",
            employee.getPosition(),
            employee.getHireDate().toString(),
            employee.getStatus()
        );
      }
      csvPrinter.flush();
    }

    return csvFile;
  }

  // 예외를 .log 파일로 저장
  private File createErrorLogFile(Exception e) throws IOException {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    String logContent = sw.toString();

    File logFile = File.createTempFile("backup-error-", ".log");
    Files.write(logFile.toPath(), logContent.getBytes(StandardCharsets.UTF_8));
    return logFile;
  }

  // File을 BinaryContent로 저장하고 ID 반환
  private Long storeAsBinary(File file, String contentType) throws IOException {
    byte[] data = Files.readAllBytes(file.toPath());
    BinaryContent binaryContent = new BinaryContent(file.getName(), contentType, (long) data.length);
    return binaryContentRepository.save(binaryContent).getId();
  }
}


