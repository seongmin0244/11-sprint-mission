package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusService userStatusService;

  @Override
  public Message create(MessageCreateRequest dto, List<UUID> attachmentIds) {
    userRepository.findById(dto.authorId())
        .orElseThrow(
            () -> new NoSuchElementException(
                "Author with id " + dto.authorId() + " does not exist"));
    channelRepository.findById(dto.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + dto.channelId() + " does not exist"));

    attachmentIds.forEach(id ->
        binaryContentRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("Attachment with id " + id + " not found")));

    Message message = new Message(dto.authorId(), dto.channelId(), dto.content(), attachmentIds);
    userStatusService.updateByUserId(dto.authorId());
    return messageRepository.save(message);
  }

  @Override
  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

    return messageRepository.findAllByChannelId(channel.getId()).stream()
        .map(m -> new MessageResponse(m.getId(), m.getAuthorId(), m.getChannelId(),
            m.getContent(), m.getAttachmentIds(), m.getCreatedAt()))
        .toList();
  }

  @Override
  public MessageResponse update(UUID id, MessageUpdateRequest dto) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));

    message.update(dto.newContent());
    messageRepository.save(message);

    userStatusService.updateByUserId(message.getAuthorId());

    return new MessageResponse(message.getId(), message.getAuthorId(), message.getChannelId(),
        message.getContent(), message.getAttachmentIds(), message.getUpdatedAt());
  }

  @Override
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + id + " not found"));

    if (!message.getAttachmentIds().isEmpty()) {
      message.getAttachmentIds()
          .forEach(binaryContentRepository::delete);
    }

    messageRepository.delete(message.getId());
  }
}