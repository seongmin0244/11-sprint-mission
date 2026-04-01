package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserDto login(LoginRequest dto) {
    // dto에서 가져온 이름으로 찾은 유저가 있다면, 유저 비밀번호와 일치하는지 확인
    User user = userRepository.findByName(dto.username())
        .orElseThrow(
            () -> new NoSuchElementException(
                "User with username " + dto.username() + " not found"));

    if (!user.getPassword().equals(dto.password())) {
      throw new IllegalArgumentException("Wrong password");
    }

    UserStatus status = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(
            () -> new NoSuchElementException("User status not found for user: " + user.getName()));

    status.updateTime(Instant.now());
    userStatusRepository.save(status); // 파일 시스템이므로 저장해 주어야 함

    return new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getName(),
        user.getEmail(), user.getProfileId(), status.isOnline());
  }
}
