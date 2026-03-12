package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String name;
    private String description;
    private ChannelType type;

    public Channel(String name, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void updateName(String name) {
        this.name = name;
        updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        updatedAt = Instant.now();
    }

    public void updateType(ChannelType type) {
        this.type = type;
        updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                "}";
    }
}
