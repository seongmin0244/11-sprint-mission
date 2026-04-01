package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;


  @Override
  public User create(UserCreateRequest dto, UUID profileId) {
    boolean isDuplicated = userRepository.findAll().values().stream()
        .anyMatch(u -> u.getName().equals(dto.username()) || u.getEmail().equals(dto.email()));
    if (isDuplicated) {
      throw new IllegalArgumentException(
          "User with email " + dto.email() + " or username " + dto.username() + " already exists"
      );
    }

    if (profileId != null && binaryContentRepository.findById(profileId)
        .isEmpty()) {
      throw new NoSuchElementException(
          "Profile image with id " + profileId + " not found");
    }

    User user = new User(dto.username(), dto.email(), dto.password(), profileId);
    User savedUser = userRepository.save(user);

    UserStatus status = new UserStatus(savedUser.getId(), Instant.now());
    userStatusRepository.save(status);

    return savedUser;
  }

  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll().values().stream().toList();

    return users.stream()
        .map(u -> {
          UserStatus status = userStatusRepository.findByUserId(u.getId())
              .orElseThrow(() -> new NoSuchElementException(
                  "UserStatus with userId " + u.getId() + " not found"));
          return new UserDto(u.getId(), u.getCreatedAt(), u.getUpdatedAt(), u.getName(),
              u.getEmail(), u.getProfileId(), status.isOnline());
        })
        .toList();
  }

  @Override
  public UserDto findById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    UserStatus status = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + id + " not found"));

    return new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getName(),
        user.getEmail(), user.getProfileId(), status.isOnline());
  }

  @Override
  public User update(UUID userId, UserUpdateRequest dto, UUID profileId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    boolean isDuplicated = userRepository.findAll().values().stream()
        // 새로 받은 정보가 나를 제외하고, 다른 객체의 이름 및 이메일과 다른지 검시
        .filter(u -> !u.getId().equals(user.getId()))
        .anyMatch(u -> u.getName().equals(dto.newUsername())
            || u.getEmail().equals(dto.newEmail()));
    if (isDuplicated) {
      throw new IllegalArgumentException(
          "User with email " + dto.newEmail() + " or username " + dto.newUsername()
              + " already exists");
    }

    UUID userProfileImageId = user.getProfileId();
    // dto로 들어온 프로필 이미지가 null이 아니고, 내 기존 프로필 이미지랑 다를 때만 검사 및 삭제
    if (profileId != null && !profileId.equals(userProfileImageId)) {
      if (binaryContentRepository.findById(profileId).isEmpty()) {
        throw new NoSuchElementException(
            "Profile image with id " + profileId + " not found");
      }
      if (userProfileImageId != null) {
        binaryContentRepository.delete(userProfileImageId);
      }
      userProfileImageId = profileId;
    }

    user.update(dto.newUsername(), dto.newEmail(), dto.newPassword(), userProfileImageId);

    return userRepository.save(user);
  }

  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    if (user.getProfileId() != null) {
      binaryContentRepository.delete(user.getProfileId());
    }

    userStatusRepository.deleteByUserId(user.getId());
    userRepository.delete(user.getId());
  }
}
