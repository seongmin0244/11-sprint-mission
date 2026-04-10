package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  public MessageDto toDto(Message message) {
    if (message == null) {
      return null;
    }

    List<BinaryContentDto> attachments = message.getAttachments().stream()
        .map(binaryContentMapper::toDto)
        .toList();

    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        userMapper.toDto(message.getAuthor()),
        message.getChannel().getId(),
        message.getContent(),
        attachments
    );
  }
}
