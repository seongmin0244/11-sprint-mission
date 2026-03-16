package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageCreateDto(
        UUID userId,
        UUID channelId,
        String content,
        List<byte[]> attachments
) {
}
