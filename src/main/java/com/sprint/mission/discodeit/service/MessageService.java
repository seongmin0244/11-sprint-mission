package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageInfoDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateDto dto);
    List<MessageInfoDto> findAllByChannelId(UUID channelId);
    MessageInfoDto update(MessageUpdateDto dto);
    void delete(UUID id);
}
