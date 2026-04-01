package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.FileLockProvider;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {

  private final FileLockProvider fileLockProvider;
  private static final String FILE_PATH = "user.ser";
  private static final Path PATH = Paths.get(FILE_PATH);

  // 락을 가져오는 메서드
  private ReentrantLock getFileLock() {
    return fileLockProvider.getLock(PATH);
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, User> load() {
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
        return (Map<UUID, User>) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("파일에서 User 데이터 불러오기 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  private void saveMapToFile(Map<UUID, User> data) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try (FileOutputStream fos = new FileOutputStream(
        FILE_PATH); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public User save(User user) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, User> data = load();
      data.put(user.getId(), user);
      saveMapToFile(data);
      return user;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Map<UUID, User> findAll() {
    return load();
  }

  @Override
  public Optional<User> findById(UUID id) {
    Map<UUID, User> data = load();
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public Optional<User> findByName(String name) {
    Map<UUID, User> data = load();
    return data.values().stream().filter(u -> u.getName().equals(name)).findAny();
  }

  @Override
  public void delete(UUID id) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      Map<UUID, User> data = load();
      if (data.remove(id) == null) {
        throw new IllegalArgumentException("[user] 없는 id 입니다.");
      }
      saveMapToFile(data);
    } finally {
      lock.unlock();
    }
  }
}
