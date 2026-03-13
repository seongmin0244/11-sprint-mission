package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateDto(
        UUID id,
        String name,
        String email,
        String password,
        byte[] profileImage
) {
}