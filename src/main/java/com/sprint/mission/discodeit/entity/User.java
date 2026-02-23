package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String name;
    private String status;

    public User(String name, String status) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();

        this.name = name;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void updateName(String name) {
        this.name = name;
        updatedAt = System.currentTimeMillis();
    }

    public void updateStatus(String status) {
        this.status = status;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                "}";
    }
}
