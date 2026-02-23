package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);
    Map<String, List<Message>> getAllMessage();
    List<Message> getMessageByChannel(String channelId);
    Message findById(UUID id);
    Message updateContent(UUID id, String content);
    Message delete(UUID id);
}
