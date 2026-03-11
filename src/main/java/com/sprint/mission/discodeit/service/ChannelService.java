package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);
    Map<String, List<Channel>> getAllChannel();
    List<Channel> findByName(String name);
    Channel findById(UUID id);
    Channel updateName(UUID id, String name);
    Channel updateDescription(UUID id, String description);
    Channel updateType(UUID id, ChannelType type);
    void delete(UUID id);
}
