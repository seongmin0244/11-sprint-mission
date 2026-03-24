package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateDto dto);
    Channel createPrivateChannel(PrivateChannelCreateDto dto);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto findById(UUID id);
    List<UUID> getUserIds(UUID id);
    Channel update(UUID id, ChannelUpdateDto dto);
    void delete(UUID id);
}
