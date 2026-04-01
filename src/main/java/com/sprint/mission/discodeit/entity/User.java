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
  private String email;
  private String password;

  private UUID profileId;

  public User(String name, String email, String password, UUID profileId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();

    this.name = name;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }

  public void update(String name, String email, String password, UUID profileId) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
    updatedAt = Instant.now();
  }


  @Override
  public String toString() {
    return "User{" +
        "username='" + name + '\'' +
        ", email='" + email + '\'' +
        ", profileId='" + profileId + '\'' +
        "}";
  }
}
