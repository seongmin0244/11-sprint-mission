package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.FileLockProvider;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

  private final FileLockProvider fileLockProvider;
  private static final String FILE_PATH = "message.ser";
  private static final Path PATH = Paths.get(FILE_PATH);

  private ReentrantLock getFileLock() {
    return fileLockProvider.getLock(PATH);
  }

  @SuppressWarnings("unchecked")
  private List<Message> load() {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      if (!Files.exists(PATH)) {
        return new ArrayList<>();
      }
      try (
          FileInputStream fis = new FileInputStream(FILE_PATH);
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        return (List<Message>) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("파일에서 메시지 데이터 불러오기 실패", e);
      }
    } finally {
      lock.unlock();
    }
  }

  private void saveListToFile(List<Message> data) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패", e);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Message save(Message message) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      List<Message> data = load();
      data.removeIf(u -> u.getId().equals(message.getId()));
      data.add(message);
      saveListToFile(data);
      return message;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public List<Message> findAll() {
    return load();
  }

  @Override
  public List<Message> findAllByChannelId(UUID chanelId) {
    return load().stream()
        .filter(m -> m.getChannelId().equals(chanelId))
        .toList();
  }

  @Override
  public Optional<Message> findById(UUID id) {
    List<Message> data = load();
    return data.stream()
        .filter(m -> m.getId().equals(id))
        .findAny();
  }

  @Override
  public Optional<Message> findLatestMessageByChannelId(UUID channelId) {
    List<Message> data = load();
    return data.stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .max(Comparator.comparing(Message::getCreatedAt));
  }

  @Override
  public void delete(UUID id) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      List<Message> data = load();
      data.removeIf(m -> m.getId().equals(id));
      saveListToFile(data);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void deleteByChannelId(UUID channelId) {
    ReentrantLock lock = getFileLock();
    lock.lock();
    try {
      List<Message> data = load();
      data.removeIf(m -> m.getChannelId().equals(channelId));
      saveListToFile(data);
    } finally {
      lock.unlock();
    }
  }
}
