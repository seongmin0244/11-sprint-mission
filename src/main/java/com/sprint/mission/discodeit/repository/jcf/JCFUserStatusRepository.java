package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus status) {
        data.put(status.getUserId(), status);
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return data.values().stream()
                .filter(us -> us.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public List<UserStatus> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        data.values().removeIf(us -> us.getId().equals(id));
    }
}
