package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.MessageService;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;

  private UUID authorId;
  private UUID channelId;
  private String content;
  private List<UUID> attachmentIds;

  public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();

    this.authorId = authorId;
    this.channelId = channelId;
    this.content = content;
    this.attachmentIds = attachmentIds;
  }

  public void update(String content) {
    this.content = content;
    updatedAt = Instant.now();
  }

  @Override
  public String toString() {
    return "Message{" +
        "content='" + content + '\'' +
        ", userName='" + authorId + '\'' +
        ", channelId='" + channelId + '\'' +
        ", attachments='" + attachmentIds + '\'' +
        "}";
  }
}