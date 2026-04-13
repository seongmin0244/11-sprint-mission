package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public MessageDto create(MessageCreateRequest dto,
      List<BinaryContentCreateRequest> binaryContentDto) {
    User author = userRepository.findById(dto.authorId())
        .orElseThrow(
            () -> new NoSuchElementException(
                "Author with id " + dto.authorId() + " does not exist"));
    Channel channel = channelRepository.findById(dto.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + dto.channelId() + " does not exist"));

    List<BinaryContent> attachments = binaryContentDto.stream()
        .map(request -> new BinaryContent(request.fileName(),
            (long) request.bytes().length, request.contentType()))
        .toList();

    attachments = binaryContentRepository.saveAll(attachments);

    for (int i = 0; i < attachments.size(); i++) {
      binaryContentStorage.put(attachments.get(i).getId(), binaryContentDto.get(i).bytes());
    }

    Message message = new Message(author, channel, dto.content(), attachments);
    messageRepository.save(message);

    UserStatus status = author.getStatus();
    status.updateTime(Instant.now());

    return messageMapper.toDto(message);
  }

  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }
    return messageRepository.findAllByChannelId(channelId).stream()
        .map(messageMapper::toDto)
        .toList();
  }

  @Override
  public MessageDto update(UUID id, MessageUpdateRequest dto) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));

    message.update(dto.newContent());
    messageRepository.save(message);

    UserStatus status = message.getAuthor().getStatus();
    status.updateTime(Instant.now());

    return messageMapper.toDto(message);
  }

  @Override
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));

    if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
      List<UUID> attachmentIds = message.getAttachments().stream()
          .map(BinaryContent::getId)
          .toList();

      binaryContentRepository.deleteAllById(attachmentIds);
    }
    messageRepository.deleteById(id);
  }
}