package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> data;

    public JCFMessageRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        data.removeIf(m -> m.getId().equals(message.getId()));
        data.add(message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[message] 없는 id 입니다."));
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        data.remove(message);
    }
}
