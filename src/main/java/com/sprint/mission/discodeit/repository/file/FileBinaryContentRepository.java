package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.FileLockProvider;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final FileLockProvider fileLockProvider;
  private static final String FILE_PATH = "binaryContent.ser";
  private static final Path PATH = Paths.get(FILE_PATH);

  private ReentrantLock getFileLock() {
    return fileLockProvider.getLock(PATH);
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, BinaryContent> load() {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      if (!Files.exists(PATH)) {
        return new HashMap<>();
      }
      try (
          FileInputStream fis = new FileInputStream(FILE_PATH);
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        return (Map<UUID, BinaryContent>) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("파일에서 BinaryContent 불러오기 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  private void saveMapToFile(Map<UUID, BinaryContent> data) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException("파일에 BinaryContent 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, BinaryContent> data = load();
      data.put(binaryContent.getId(), binaryContent);
      saveMapToFile(data);
      return binaryContent;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(load().get(id));
  }

  @Override
  public void delete(UUID id) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, BinaryContent> data = load();
      data.remove(id);
      saveMapToFile(data);
    } finally {
      lock.unlock();
    }
  }
}
