package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.StatusUpdateDto;
import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.List;
import java.util.UUID;

public class UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatusService(UserStatusRepository userStatusRepository, UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    public UserStatus create(UserStatusCreateDto dto){
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("없는 user id 입니다."));
        if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 UserStatus 입니다.");
        }
        UserStatus userStatus = new UserStatus(user.getId());
        return userStatusRepository.save(userStatus);
    }

    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 userStatus id 입니다."));
    }

    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    public UserStatus update(StatusUpdateDto dto) {
        UserStatus userStatus = userStatusRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("없는 userStatus id 입니다."));

        userStatus.updateTime();

        return userStatusRepository.save(userStatus);
    }

    public UserStatus updateByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("없는 user id 입니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("없는 userStatus id 입니다."));

        userStatus.updateTime();

        return userStatusRepository.save(userStatus);
    }

    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 userStatus id 입니다."));

        userStatusRepository.delete(userStatus.getId());
    }
}
