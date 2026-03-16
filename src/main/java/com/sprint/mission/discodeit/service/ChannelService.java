package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateDto dto);
    Channel createPrivateChannel(PrivateChannelCreateDto dto);
    List<ChannelInfoDto> findAllByUserId(UUID userId);
    List<Channel> findByName(String name);
    ChannelInfoDto findById(UUID id);
    ChannelInfoDto update(ChannelUpdateDto dto);
    void delete(UUID id);
}
