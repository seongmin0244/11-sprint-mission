package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService{

    private final List<Message> data;

    public JCFMessageService(List<Message> data) {
        this.data = data;
    }

    @Override
    public Message create(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public Map<String, List<Message>> getAllMessage() {
        return data.stream()
                .collect(Collectors.groupingBy(Message::getChannelName));
    }

    @Override
    public List<Message> getMessageByChannel(String channelId) {
        return data.stream()
                .filter(m -> m.getChannelName().equals(channelId))
                .toList();

    }

    @Override
    public Message findById(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[message] 없는 id 입니다."));
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = findById(id);
        message.updateContent(content);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        data.remove(message);
    }
}
