package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService{

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateDto dto) {
        boolean isDuplicated = getAllUser().stream()
                .anyMatch(u -> u.getName().equals(dto.name()) || u.getEmail().equals(dto.email()));
        if (isDuplicated) {
            throw new IllegalArgumentException("이미 가입된 유저입니다.");
        }

        UUID profileImageId = null;
        if (dto.profileImage() != null) {
            BinaryContent image = new BinaryContent(dto.profileImage());
            binaryContentRepository.save(image);
            profileImageId = image.getId();
        }

        User user = new User(dto.name(), dto.email(), dto.password(), profileImageId);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

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

//    @Override
//    public User updateStatus(UUID id, String status) {
//        User user = userRepository.findById(id);
//        user.updateStatus(status);
//        return userRepository.save(user);
//    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
