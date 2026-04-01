package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final BinaryContentService binaryContentService;


  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> create(
      @RequestPart("userCreateRequest") UserCreateRequest dto,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    UUID profileId = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest)
        .map(binaryContentService::create)
        .map(BinaryContent::getId)
        .orElse(null);

    User u = userService.create(dto, profileId);

    UserResponse response = new UserResponse(u.getId(), u.getCreatedAt(), u.getUpdatedAt(),
        u.getName(), u.getEmail(), u.getPassword(), u.getProfileId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> update(@PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest dto,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    UUID profileId = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest)
        .map(binaryContentService::create)
        .map(BinaryContent::getId)
        .orElse(null);

    User u = userService.update(userId, dto, profileId);

    UserResponse response = new UserResponse(u.getId(), u.getCreatedAt(), u.getUpdatedAt(),
        u.getName(), u.getEmail(), u.getPassword(), u.getProfileId());

    return ResponseEntity.ok(response);

  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  // 단건 조회 추가

  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> userDtoList = userService.findAll();
    return ResponseEntity.ok(userDtoList);
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateStatus(@PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest dto) {
    UserStatus userStatus = userStatusService.update(userId, dto);
    return ResponseEntity.ok(userStatus);
  }

  // create()에서 파일을 받아 BinaryContentCreateRequest에 담아주는 메서드
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile multipartFile) {
    if (multipartFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest dto = new BinaryContentCreateRequest(
            multipartFile.getOriginalFilename(),
            multipartFile.getContentType(),
            multipartFile.getBytes()
        );
        return Optional.of(dto);
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Failed to process the uploaded file", e);
      }
    }
  }
}
