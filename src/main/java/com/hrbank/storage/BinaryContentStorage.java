package com.hrbank.storage;

import com.hrbank.dto.binarycontent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "hrbank.storage.type", value = "local")
public class BinaryContentStorage {
  private final Path root;

  public BinaryContentStorage(@Value(".hrbank/storage") Path root){
    this.root = root;
  }

  @PostConstruct
  public void init(){
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // 실제 파일 Storage에 저장
  public void put(Long id, byte[] data){
    Path filePath = root.resolve(id.toString());
    if (Files.exists(filePath)) {
      throw new RuntimeException("이미 존재하는 파일");
    }
    try (OutputStream fos = Files.newOutputStream(filePath)) {
      fos.write(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // id로 실제 파일 찾기
  public InputStream get(Long id){
    Path filePath = root.resolve(id.toString());
    if (Files.exists(filePath)) {
      throw new RuntimeException("존재하지 않는 파일");
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 중 실패: " + e);
    }
  }

  // 파일 다운로드
  public ResponseEntity<Resource> download(BinaryContentDto contentDto) {
    InputStream downloadData = get(contentDto.id());
    Resource resource = new InputStreamResource(downloadData);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + contentDto.fileName() + "\"")
        .body(resource);
  }

}
