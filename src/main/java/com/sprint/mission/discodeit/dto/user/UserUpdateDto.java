package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateDto(
        String name,
        String email,
        String password,
        UUID profileImageId
) {
}