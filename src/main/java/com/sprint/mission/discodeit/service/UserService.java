package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateDto dto);
    List<UserDto> findAll();
    UserDto findById(UUID id);
    User update(UUID id, UserUpdateDto dto);
    void delete(UUID id);
}
