package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements
    com.sprint.mission.discodeit.service.ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest dto) {
    User user = userRepository.findById(dto.userId())
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + dto.userId() + " does not exist"));
    Channel channel = channelRepository.findById(dto.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + dto.channelId() + " does not exist"));

    if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
      throw new IllegalArgumentException(
          "ReadStatus with userId " + user.getId() + " and channelId " + channel.getId()
              + " already exists");
    }

    ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId(), Instant.now());

    return readStatusRepository.save(readStatus);
  }

  @Override
  public ReadStatus find(UUID id) {
    return readStatusRepository.find(id)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    return readStatusRepository.findAllByUserId(user.getId());
  }

  // 채팅방 읽음
  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest dto) {
    ReadStatus readStatus = readStatusRepository.find(readStatusId)
        .orElseThrow(() -> new NoSuchElementException(
            "ReadStatus with id " + readStatusId + " not found"));

    readStatus.updateLastReadAt();

    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID id) {
    ReadStatus readStatus = readStatusRepository.find(id)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
    readStatusRepository.delete(readStatus.getId());
  }
}
