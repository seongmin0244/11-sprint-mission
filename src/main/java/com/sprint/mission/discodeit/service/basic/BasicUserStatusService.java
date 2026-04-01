package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest dto) {
    User user = userRepository.findById(dto.userId())
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + dto.userId() + " not found"));
    if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
      throw new IllegalArgumentException(
          "UserStatus with userId " + user.getId() + " already exists");
    }

    UserStatus userStatus = new UserStatus(user.getId(), dto.lastActiveAt());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus findByUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    return userStatusRepository.findByUserId(user.getId())
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(UUID userId, UserStatusUpdateRequest dto) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userId + " not found"));
    userStatus.updateTime(dto.newLastActiveAt());

    return userStatusRepository.save(userStatus);
  }

  @Override
  public void updateByUserId(UUID userId) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));

    userStatus.updateTime(Instant.now());
    userStatusRepository.save(userStatus);
  }

  @Override
  public void delete(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    userStatusRepository.delete(userStatus.getId());
  }
}
