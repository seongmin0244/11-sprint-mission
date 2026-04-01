package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.FileLockProvider;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {

  private final FileLockProvider fileLockProvider;
  private static final String FILE_PATH = "userStatus.ser";
  private static final Path PATH = Paths.get(FILE_PATH);


  // 락을 가져오는 메서드
  private ReentrantLock getFileLock() {
    return fileLockProvider.getLock(PATH);
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, UserStatus> load() {
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
        return (Map<UUID, UserStatus>) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("파일에서 UserStatus 데이터 불러오기 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  private void saveMapToFile(Map<UUID, UserStatus> data) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException("파일에 UserStatus 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }

  // 맵의 키를 userId로 지정
  @Override
  public UserStatus save(UserStatus status) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, UserStatus> data = load();
      data.put(status.getUserId(), status);
      saveMapToFile(data);
      return status;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    return load().values().stream()
        .filter(us -> us.getId().equals(id))
        .findAny();
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return Optional.ofNullable(load().get(userId));
  }

  @Override
  public List<UserStatus> findAll() {
    return load().values().stream().toList();
  }

  @Override
  public void delete(UUID id) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, UserStatus> data = load();
      data.values().removeIf(us -> us.getId().equals(id));
      saveMapToFile(data);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void deleteByUserId(UUID userId) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, UserStatus> data = load();
      data.remove(userId);
      saveMapToFile(data);
    } finally {
      lock.unlock();
    }
  }
}
