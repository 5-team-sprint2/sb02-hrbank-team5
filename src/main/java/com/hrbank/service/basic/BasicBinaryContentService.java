package com.hrbank.service.basic;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.entity.BinaryContent;
import com.hrbank.entity.Employee;
import com.hrbank.mapper.BinaryContentMapper;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import com.hrbank.mapper.EmployeeMapper;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.repository.EmployeeRepository;
import com.hrbank.service.BinaryContentService;
import com.hrbank.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.servers.Server;
import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Server
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @Override
  @Transactional
  public BinaryContent create(BinaryContentCreateRequest request) {
    String originalFilename = request.file().getOriginalFilename();
    String contentType = request.file().getContentType();
    long size = request.file().getSize();
    byte[] bytes;
    try {
      bytes = request.file().getBytes();
    } catch (IOException e) {
      throw new RestException(ErrorCode.PROFILE_IMAGE_NOT_FOUND);
    }
    BinaryContent binaryContent = new BinaryContent(originalFilename, contentType, size);
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);
    return binaryContent;
  }

  @Override
  public BinaryContentDto findById(Long id){
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(id + " 에 해당하는 BinaryContent를 찾을 수 없음"));
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RestException(ErrorCode.PROFILE_IMAGE_NOT_FOUND));

    if (employee.getProfileImage() != null) {
      try {
        binaryContentStorage.deleteProfileImage(employee.getProfileImage().getId());
      } catch (IOException e) {
        throw new RestException(ErrorCode.FILE_DELETE_FAILED);
      }
    }

    employeeRepository.delete(employee);
  }
}

