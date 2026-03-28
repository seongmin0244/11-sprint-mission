package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID userId,
        UUID channelId,
        String content,
        List<UUID> attachments
) {
}
