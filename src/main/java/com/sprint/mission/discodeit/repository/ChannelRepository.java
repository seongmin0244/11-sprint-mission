package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Map<UUID, Channel> findAll();
    Channel findById(UUID id);
    void delete(UUID id);

}
