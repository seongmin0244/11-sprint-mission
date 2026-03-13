package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserInformationDto(
        UUID id,
        String name,
        String email,
        UUID profileImage,
        boolean isOnline
) {
}
