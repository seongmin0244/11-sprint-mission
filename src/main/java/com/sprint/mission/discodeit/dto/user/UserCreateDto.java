package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserCreateDto(
        String name,
        String email,
        String password,
        UUID profileImageId
) {
}


