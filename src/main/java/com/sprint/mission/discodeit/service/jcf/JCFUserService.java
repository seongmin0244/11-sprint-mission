package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService{
    private final Map<UUID, User> data;

    public JCFUserService(Map<UUID, User> data) {
        this.data = data;
    }

    @Override
    public User create(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return data.values().stream().toList();
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = findById(id);
        user.updateName(name);
        return user;
    }

    @Override
    public User updateStatus(UUID id, String status) {
        User user = findById(id);
        user.updateStatus(status);
        return user;
    }

    @Override
    public User delete(UUID id) {
        data.remove(id);
        return data.get(id);
    }
}
