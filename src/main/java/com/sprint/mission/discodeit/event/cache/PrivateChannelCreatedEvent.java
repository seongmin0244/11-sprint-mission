package com.sprint.mission.discodeit.event.cache;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreatedEvent(
    List<UUID> participantIds
) {

}
