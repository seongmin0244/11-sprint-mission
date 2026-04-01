package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements
    com.sprint.mission.discodeit.service.BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  public BinaryContent create(BinaryContentCreateRequest dto) {
    if (dto.bytes() == null) {
      throw new IllegalArgumentException("File content is required");
    }
    String fileName = dto.fileName();
    byte[] bytes = dto.bytes();
    String contentType = dto.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType,
        bytes
    );
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  public BinaryContent find(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> uuids) {
    if (uuids == null || uuids.isEmpty()) {
      return List.of();
    }
    return uuids.stream()
        .map(binaryContentRepository::findById)
        .flatMap(Optional::stream)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
    binaryContentRepository.delete(binaryContent.getId());
  }
}
