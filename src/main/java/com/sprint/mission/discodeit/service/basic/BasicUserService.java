package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService{

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        boolean flag = getAllUser().stream()
                .anyMatch(u -> u.getName().equals(user.getName()));
        if (flag) {
            throw new IllegalArgumentException("이미 가입된 유저입니다.");
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll().values().stream()
                .toList();
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = userRepository.findById(id);
        boolean flag = getAllUser().stream()
                .filter(u -> !u.getId().equals(id))
                .anyMatch(u -> u.getName().equals(name));
        if (flag) {
            throw new IllegalArgumentException("이미 가입된 닉네임입니다.");
        }
        user.updateName(name);
        return userRepository.save(user);
    }

    @Override
    public User updateStatus(UUID id, String status) {
        User user = userRepository.findById(id);
        user.updateStatus(status);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
