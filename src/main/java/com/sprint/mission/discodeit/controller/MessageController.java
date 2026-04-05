package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Message")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  @Operation(summary = "Message 생성", operationId = "create_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음"),
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨")
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @Valid
      @RequestPart("messageCreateRequest") MessageCreateRequest dto,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    List<UUID> attachmentIds = Optional.ofNullable(attachments)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::resolveAttachmentRequest) // 파일을 Optional로 감싸진 DTO 상자로 변환
        .flatMap(Optional::stream) // 텅 빈 상자(빈 파일)는 버리고 알맹이만 다음으로 넘김
        .map(binaryContentService::create)
        .map(BinaryContent::getId)
        .toList();

    Message m = messageService.create(dto, attachmentIds);

    MessageResponse messageResponse = new MessageResponse(m.getId(), m.getAuthorId(),
        m.getChannelId(), m.getContent(), m.getAttachmentIds(), m.getCreatedAt());
    return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
  }

  @Operation(summary = "Message 내용 수정", operationId = "update_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> update(@Valid @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest dto) {
    MessageResponse messageResponse = messageService.update(messageId, dto);
    return ResponseEntity.ok(messageResponse);
  }

  @Operation(summary = "Message 삭제", operationId = "delete_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  @DeleteMapping(value = "/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<MessageResponse>> findAll(@RequestParam("channelId") UUID channelId) {
    List<MessageResponse> messageResponseList = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok(messageResponseList);
  }

  private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(MultipartFile attachment) {
    if (attachment.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest dto = new BinaryContentCreateRequest(
            attachment.getOriginalFilename(),
            attachment.getContentType(),
            attachment.getBytes() // 예외처리 필수
        );
        return Optional.of(dto);
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Failed to process the uploaded file", e);
      }
    }
  }
}
