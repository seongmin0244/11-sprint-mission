package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID userId,
        UUID channelId,
        String content,
        List<UUID> attachments
) {
}
