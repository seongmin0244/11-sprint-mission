package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Disabled // 후에 Mockito 적용 고려
public class S3BinaryContentStorageTest {

  private S3BinaryContentStorage storage;
  private String testBucket;

  @BeforeEach
  void setup() throws IOException {
    Properties properties = new Properties();

    try (FileInputStream fis = new FileInputStream(".env")) {
      properties.load(fis);
    }
    testBucket = properties.getProperty("AWS_S3_BUCKET");

    storage = new S3BinaryContentStorage(
        properties.getProperty("AWS_S3_ACCESS_KEY"),
        properties.getProperty("AWS_S3_SECRET_KEY"),
        properties.getProperty("AWS_S3_REGION"),
        testBucket,
        10L
    );
  }

  @Test
  @DisplayName("바이너리 데이터를 S3에 업로드하면, 요청한 UUID와 동일한 UUID를 반환해야 한다.")
  void putTest() {
    //given
    UUID id = UUID.randomUUID();
    byte[] bytes = "S3 Upload Test".getBytes();

    //when
    UUID result = storage.put(id, bytes);

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(id);
  }

  @Test
  @DisplayName("S3에 저장된 파일을 UUID로 조회하면, 정상적인 InputStream을 반환해야 한다.")
  void getTest() throws IOException {
    //given
    UUID id = UUID.randomUUID();
    String testString = "S3 Upload Test";
    storage.put(id, testString.getBytes());

    //when (try-with-resource 구문 사용)
    try (InputStream result = storage.get(id)) {
      //then
      assertThat(result).isNotNull();
      assertThat(result).hasContent(testString);
    }
  }

  @Test
  @DisplayName("파일 다운로드를 요청하면, 302 FOUND 상태 코드와 Presigned URL이 담긴 응답을 반환해야 한다.")
  void downloadTest() {
    //given
    UUID id = UUID.randomUUID();
    BinaryContentDto binaryContentDto = new BinaryContentDto(id, "testFile", 1L, ".txt");

    //when
    ResponseEntity<?> result = storage.download(binaryContentDto);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(result.getHeaders().getLocation()).isNotNull();
  }
}
