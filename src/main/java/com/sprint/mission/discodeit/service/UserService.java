package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

  User create(UserCreateRequest dto, UUID profileId);

  List<UserDto> findAll();

  UserDto findById(UUID id);

  User update(UUID id, UserUpdateRequest dto, UUID profileId);

  void delete(UUID id);
}
