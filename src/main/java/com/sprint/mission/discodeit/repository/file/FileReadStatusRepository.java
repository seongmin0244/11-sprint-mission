package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.FileLockProvider;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {

  private final FileLockProvider fileLockProvider;
  private static final String FILE_PATH = "readStatus.ser";
  private static final Path PATH = Paths.get(FILE_PATH);

  private ReentrantLock getFileLock() {
    return fileLockProvider.getLock(PATH);
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, ReadStatus> load() {
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
        return (Map<UUID, ReadStatus>) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("파일에서 ReadStatus 데이터 불러오기 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  private void saveMapToFile(Map<UUID, ReadStatus> data) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException("파일에 ReadStatus 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public ReadStatus save(ReadStatus status) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, ReadStatus> data = load();
      data.put(status.getId(), status);
      saveMapToFile(data);
      return status;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return load().values().stream()
        .filter(rs -> rs.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return load().values().stream()
        .filter(rs -> rs.getUserId().equals(userId))
        .toList();
  }

  @Override
  public Optional<ReadStatus> find(UUID id) {
    return Optional.ofNullable(load().get(id));
  }

  @Override
  public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
    return load().values().stream()
        .anyMatch(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId));
  }

  @Override
  public void delete(UUID id) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, ReadStatus> data = load();
      data.remove(id);
      saveMapToFile(data);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void deleteByChannelId(UUID channelId) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, ReadStatus> data = load();
      data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
      saveMapToFile(data);
    } finally {
      lock.unlock();
    }
  }
}
