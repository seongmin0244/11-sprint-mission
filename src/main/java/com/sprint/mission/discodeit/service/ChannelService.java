package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublicChannel(PublicChannelCreateRequest dto);

  Channel createPrivateChannel(PrivateChannelCreateRequest dto);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto findById(UUID id);

  List<UUID> getUserIds(UUID id);

  Channel update(UUID id, PublicChannelUpdateRequest dto);

  void delete(UUID id);
}
