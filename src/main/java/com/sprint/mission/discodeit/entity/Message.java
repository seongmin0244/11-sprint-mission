package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.MessageService;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private UUID userId;
    private UUID channelId;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();

        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", userName='" + userId + '\'' +
                ", channelId='" + channelId + '\'' +
                "}";
    }
}