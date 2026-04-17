package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;


@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {
    if (channel == null) {
      return null;
    }

    Instant lastMessageAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
            channel.getId())
        .map(Message::getCreatedAt)
        .orElse(null);

    List<UserDto> participants = readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map(rs -> userMapper.toDto(rs.getUser()))
        .toList();

    return new ChannelDto(
        channel.getId(),
        channel.getName(),
        channel.getDescription(),
        channel.getType(),
        lastMessageAt,
        participants
    );
  }
}
