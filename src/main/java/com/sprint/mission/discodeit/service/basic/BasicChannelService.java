package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public Map<String, List<Channel>> getAllChannel() {
        Map<UUID, Channel> data = channelRepository.findAll();
        return data.values().stream()
                .collect(Collectors.groupingBy(Channel::getName));
    }

    @Override
    public List<Channel> findByName(String name) {
        Map<UUID, Channel> data = channelRepository.findAll();
        return data.values().stream()
                .filter(c -> c.getName().equals(name))
                .toList();
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = channelRepository.findById(id);
        channel.updateName(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = channelRepository.findById(id);
        channel.updateDescription(description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateType(UUID id, ChannelType type) {
        Channel channel = channelRepository.findById(id);
        channel.updateType(type);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.delete(id);
    }
}
