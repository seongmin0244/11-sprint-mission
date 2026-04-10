package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;


  @Override
  public UserDto create(UserCreateRequest dto,
      Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
    if (userRepository.existsByUsernameOrEmail(dto.username(), dto.email())) {
      throw new IllegalArgumentException(
          "User with email " + dto.email() + " or username " + dto.username() + " already exists"
      );
    }

    BinaryContent profile = null;
    if (binaryContentCreateRequest.isPresent()) {
      BinaryContentCreateRequest profileRequest = binaryContentCreateRequest.get();
      profile = new BinaryContent(profileRequest.fileName(), (long) profileRequest.bytes().length,
          profileRequest.contentType(),
          profileRequest.bytes());
      profile = binaryContentRepository.save(profile);
    }

    User user = new User(dto.username(), dto.email(), dto.password(), profile);
    User savedUser = userRepository.save(user);

    UserStatus status = new UserStatus(user, Instant.now());
    userStatusRepository.save(status);

    return userMapper.toDto(savedUser);
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  public UserDto findById(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    return userMapper.toDto(user);
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest dto,
      Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    // 새로 받은 정보가 나를 제외하고, 다른 객체의 이름 및 이메일과 다른지 검사
    if (userRepository.existsByUsernameAndIdNot(dto.newUsername(), userId)
        || userRepository.existsByEmailAndIdNot(dto.newEmail(), userId)) {
      throw new IllegalArgumentException(
          "User with email " + dto.newEmail() + " or username " + dto.newUsername()
              + " already exists");
    }

    BinaryContent profile = user.getProfile();
    if (binaryContentCreateRequest.isPresent()) {

      if (user.getProfile() != null) {
        binaryContentRepository.delete(user.getProfile());
      }

      BinaryContentCreateRequest profileRequest = binaryContentCreateRequest.get();
      profile = new BinaryContent(profileRequest.fileName(), (long) profileRequest.bytes().length,
          profileRequest.contentType(),
          profileRequest.bytes());
      profile = binaryContentRepository.save(profile);
    }

    user.update(dto.newUsername(), dto.newEmail(), dto.newPassword(), profile);

    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    if (user.getProfile() != null) {
      binaryContentRepository.delete(user.getProfile());
    }

    userRepository.deleteById(id);
  }
}
