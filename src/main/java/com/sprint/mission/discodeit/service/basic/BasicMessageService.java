package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
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

    @Override
    public Message create(MessageCreateRequest dto) {
        userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("없는 user id 입니다."));
        channelRepository.findById(dto.channelId())
                .orElseThrow(() -> new IllegalArgumentException("없는 channel id 입니다."));

        List<UUID> attachmentIds = List.of();
        if (dto.attachments()!= null) {
            attachmentIds = dto.attachments().stream()
                    .map(bytes -> {
                        BinaryContent bc = new BinaryContent(bytes);
                        binaryContentRepository.save(bc);
                        return bc.getId();
                    })
                    .toList();
        }

        Message message = new Message(dto.userId(), dto.channelId(), dto.content(), attachmentIds);

        return messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("없는 channel id 입니다."));

        return messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(m -> new MessageResponse(m.getId(), m.getUserId(), m.getChannelId(),
                            m.getContent(), m.getAttachmentIds()))
                .toList();
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest dto) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 message id 입니다."));

        message.update(dto.content());
        messageRepository.save(message);

        return new MessageResponse(message.getId(), message.getUserId(), message.getChannelId(),
                message.getContent(), message.getAttachmentIds());
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 message id 입니다."));

        if (!message.getAttachmentIds().isEmpty()) {
            message.getAttachmentIds()
                    .forEach(binaryContentRepository::delete);
        }

        messageRepository.delete(message.getId());
    }
}