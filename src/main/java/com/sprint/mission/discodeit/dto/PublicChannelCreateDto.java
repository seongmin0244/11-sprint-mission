package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelCreateDto(
        String name,
        String description
) {
}
