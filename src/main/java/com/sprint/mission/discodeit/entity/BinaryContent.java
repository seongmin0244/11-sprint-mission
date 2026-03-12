package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private UUID id;
    private Instant createdAt;

    private byte[] bytes;

    public BinaryContent(byte[] image) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.bytes = image;
    }
}
