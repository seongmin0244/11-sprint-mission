package com.sprint.mission.discodeit.dto;

public record UserCreateDto(
        String name,
        String email,
        String password,
        byte[] profileImage
) {}


