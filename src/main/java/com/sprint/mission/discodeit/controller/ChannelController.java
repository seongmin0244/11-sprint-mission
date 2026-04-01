package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest dto) {
    Channel c = channelService.createPublicChannel(dto);
    ChannelResponse response = new ChannelResponse(c.getId(), c.getCreatedAt(), c.getUpdatedAt(),
        ChannelType.PUBLIC, c.getName(), c.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest dto) {
    Channel c = channelService.createPrivateChannel(dto);
    ChannelResponse response = new ChannelResponse(c.getId(), c.getCreatedAt(), c.getUpdatedAt(),
        ChannelType.PRIVATE, c.getName(), c.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(@Valid @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest dto) {
    Channel c = channelService.update(channelId, dto);
    ChannelResponse response = new ChannelResponse(c.getId(), c.getCreatedAt(), c.getUpdatedAt(),
        c.getType(), c.getName(), c.getDescription());
    return ResponseEntity.ok(response);

  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channelDtoList = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channelDtoList);
  }
}
