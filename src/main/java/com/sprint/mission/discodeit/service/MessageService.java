package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);
    List<String> getAllMessage();
    List<String> getMessageByChannel(UUID channelId);
    //Message findById(UUID id);
    String getUserName(UUID userId);
    String getChannelName(UUID channelId);
    String updateContent(UUID id, String content);
    void delete(UUID id);
    String printMessage(Message message);
}
