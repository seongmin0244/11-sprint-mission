package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    public Map<String, List<Channel>> getAllChannel() {
        Map<UUID, Channel> data = channelRepository.findAll();
        return data.values().stream()
                .collect(Collectors.groupingBy(Channel::getName));
    }

    public List<Channel> findByName(String name) {
        Map<UUID, Channel> data = channelRepository.findAll();
        return data.values().stream()
                .filter(c -> c.getName().equals(name))
                .toList();
    }

    public Channel updateName(UUID id, String name) {
        Channel channel = channelRepository.findById(id);
        channel.updateName(name);
        return channelRepository.save(channel);
    }

    public Channel updateDescription(UUID id, String description) {
        Channel channel = channelRepository.findById(id);
        channel.updateDescription(description);
        return channelRepository.save(channel);
    }

    public Channel updateType(UUID id, String type) {
        Channel channel = channelRepository.findById(id);
        channel.updateType(type);
        return channelRepository.save(channel);
    }

    public void delete(UUID id) {
        channelRepository.delete(id);
    }
}
