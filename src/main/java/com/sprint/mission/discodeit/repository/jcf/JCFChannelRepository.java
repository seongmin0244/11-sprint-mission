package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        return data.put(channel.getId(), channel);
    }

    @Override
    public Map<UUID, Channel> findAll() {
        return data;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("[channel] 없는 id 입니다.");
        }
        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new IllegalArgumentException("[channel] 없는 id 입니다.");
        }
    }
}
