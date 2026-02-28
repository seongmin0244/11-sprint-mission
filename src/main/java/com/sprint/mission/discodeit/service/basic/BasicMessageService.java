package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class BasicMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public Message create(Message message) {
        // 이미 있는 유저와 채널인지 확인 후 저장
        getUserName(message.getUserId());
        getChannelName(message.getChannelId());
        return messageRepository.save(message);
    }

    public List<String> getAllMessage() {
        return messageRepository.findAll().stream()
                .map(this::printMessage)
                .toList();
    }

    public List<String> getMessageByChannel(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::printMessage)
                .toList();
    }

    public String getUserName(UUID userId) {
        User user = userRepository.findById(userId);
        return user.getName();
    }

    public String getChannelName(UUID channelId) {
        Channel channel = channelRepository.findById(channelId);
        return channel.getName();
    }

    public String updateContent(UUID id, String content) {
        Message message = messageRepository.findById(id);
        message.updateContent(content);
        messageRepository.save(message);
        return printMessage(message);
    }

    public void delete(UUID id) {
        messageRepository.delete(id);
    }

    public String printMessage(Message message) {
        return "Message{" +
                "userName='" + getUserName(message.getUserId()) + '\'' +
                ", channelId='" + getChannelName(message.getChannelId()) + '\'' +
                ", content='" + message.getContent() + '\'' +
                "}";
    }
}