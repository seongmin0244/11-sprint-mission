package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService {

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll().values().stream()
                .toList();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public User updateName(UUID id, String name) {
        User user = userRepository.findById(id);
        user.updateName(name);
        return userRepository.save(user);
    }

    public User updateStatus(UUID id, String status) {
        User user = userRepository.findById(id);
        user.updateStatus(status);
        return userRepository.save(user);
    }

    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
