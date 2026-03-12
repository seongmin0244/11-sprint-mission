package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus status);
    UserStatus findById(UUID id);
}
