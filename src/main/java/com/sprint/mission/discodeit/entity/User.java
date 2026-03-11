package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

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
