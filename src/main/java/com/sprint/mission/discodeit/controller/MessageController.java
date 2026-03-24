package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDto> create(@RequestBody MessageCreateDto dto) {
        Message m = messageService.create(dto);
        MessageDto messageDto = new MessageDto(m.getId(), m.getUserId(), m.getChannelId(), m.getContent(), m.getAttachmentIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<MessageDto> update(@PathVariable UUID id,
                             @RequestBody MessageUpdateDto dto) {
        MessageDto messageDto = messageService.update(id, dto);
        return ResponseEntity.ok(messageDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto>> findAll(@RequestParam("channelId") UUID channelId) {
        List<MessageDto> messageDtoList = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messageDtoList);
    }
}
