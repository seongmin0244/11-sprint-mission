package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateDto dto) {
        Channel c = channelService.createPublicChannel(dto);
        ChannelDto infoDto = new ChannelDto(c.getId(), c.getName(), c.getDescription(), ChannelType.PUBLIC, c.getUpdatedAt(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(infoDto);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateDto dto) {
        Channel c = channelService.createPrivateChannel(dto);
        ChannelDto infoDto = new ChannelDto(c.getId(), c.getName(), c.getDescription(), ChannelType.PRIVATE, c.getUpdatedAt(), dto.users());
        return ResponseEntity.status(HttpStatus.CREATED).body(infoDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ChannelDto> update(@PathVariable UUID id,
                                             @RequestBody ChannelUpdateDto dto) {
        Channel c = channelService.update(id, dto);
        List<UUID> userIds = channelService.getUserIds(c.getId());
        ChannelDto infoDto = new ChannelDto(c.getId(), c.getName(), c.getDescription(), c.getType(), c.getUpdatedAt(), userIds);
        return ResponseEntity.ok(infoDto);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channelDtoList = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channelDtoList);
    }
}
