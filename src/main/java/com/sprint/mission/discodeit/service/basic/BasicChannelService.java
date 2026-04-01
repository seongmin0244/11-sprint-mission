package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public Channel createPublicChannel(PublicChannelCreateRequest dto) {
    Channel channel = new Channel(dto.name(), dto.description(), ChannelType.PUBLIC);
    return channelRepository.save(channel);
  }

  @Override
  public Channel createPrivateChannel(PrivateChannelCreateRequest dto) {
    if (dto.participantIds() == null || dto.participantIds().size() < 2) {
      throw new IllegalArgumentException(
          "At least 2 users are required to create a private channel");
    }
    Channel channel = new Channel(null, null, ChannelType.PRIVATE);
    dto.participantIds().forEach(id -> {
      User user = userRepository.findById(id)
          .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
      ReadStatus status = new ReadStatus(user.getId(), channel.getId(), Instant.now());
      readStatusRepository.save(status);
    });
    channelRepository.save(channel);
    return channel;
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // 한 유저가 속한 PRIVATE 채팅방과, 공개방인 PUBLIC 채팅방 목록을 보여주는 메서드
    // refactor: 비효율 로직 개선 - findAll은 한 번만 쓰고 filter로 골라내기
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Set<UUID> participatedChannels = readStatusRepository.findAllByUserId(user.getId()).stream()
        .map(ReadStatus::getChannelId)
        .collect(Collectors.toSet());

    return channelRepository.findAll().values().stream()
        .filter(c -> c.getType().equals(ChannelType.PUBLIC) ||
            participatedChannels.contains(c.getId()))
        .map(this::toDto)
        .toList();
  }

  @Override
  public ChannelDto findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    return toDto(channel);
  }

  @Override
  public List<UUID> getUserIds(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    List<ReadStatus> readStatusList = readStatusRepository.findAllByChannelId(channel.getId());
    return readStatusList.stream()
        .map(ReadStatus::getUserId)
        .toList();
  }

  @Override
  public Channel update(UUID id, PublicChannelUpdateRequest dto) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(dto.newName(), dto.newDescription());
    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    readStatusRepository.deleteByChannelId(channel.getId());
    messageRepository.deleteByChannelId(channel.getId());
    channelRepository.delete(channel.getId());
  }

  private ChannelDto toDto(Channel channel) {
    Instant latestMessageTime = messageRepository.findLatestMessageByChannelId(channel.getId())
        .map(Message::getCreatedAt)
        .orElse(null);

    List<UUID> userIdList = List.of();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      userIdList = readStatusRepository.findAllByChannelId(channel.getId()).stream()
          .map(ReadStatus::getUserId)
          .toList();
    }

    return new ChannelDto(channel.getId(), channel.getName(), channel.getDescription(),
        channel.getType(), latestMessageTime, userIdList);
  }
}
