package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);
    List<User> getAllUser();
    User findById(UUID id);
    User updateName(UUID id, String name);
    User updateStatus(UUID id, String status);
    User delete(UUID id);
}
