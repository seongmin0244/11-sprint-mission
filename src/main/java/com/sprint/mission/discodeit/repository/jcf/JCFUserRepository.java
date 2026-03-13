package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<UUID, User> findAll() {
        return data;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);
        if (user == null) {
            throw new IllegalArgumentException("[user] 없는 id 입니다.");
        }
        return user;
    }

    @Override
    public Optional<User> findByName(String name) {
        return data.values().stream()
                .filter(u -> u.getName().equals(name))
                .findAny();
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new IllegalArgumentException("[user] 없는 id 입니다.");
        }
    }
}
