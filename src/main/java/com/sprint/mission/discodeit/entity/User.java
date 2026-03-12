package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String name;
    private String status;

    public User(String name, String status) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.name = name;
        this.status = status;
    }

    public void updateName(String name) {
        this.name = name;
        updatedAt = Instant.now();
    }

    public void updateStatus(String status) {
        this.status = status;
        updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                "}";
    }
}
