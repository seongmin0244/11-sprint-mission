package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateDto dto);
    List<User> getAllUser();
    User findById(UUID id);
    User updateName(UUID id, String name);
    void delete(UUID id);
}
