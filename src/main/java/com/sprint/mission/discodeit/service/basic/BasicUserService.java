package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserInformationDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
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
        boolean isDuplicated = findAll().stream()
                .anyMatch(u -> u.name().equals(dto.name()) || u.email().equals(dto.email()));
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
    public List<UserInformationDto> findAll() {
        List<User> users = userRepository.findAll().values().stream().toList();

        List<UserInformationDto> dtos = users.stream()
                .map(u -> {
                    UserStatus status = userStatusRepository.findById(u.getId());
                    return new UserInformationDto(u.getId(), u.getName(), u.getEmail(), u.getProfileImageId(), status.isOnline());
                })
                .toList();
        return dtos;
    }

    @Override
    public UserInformationDto findById(UUID id) {
        User user = userRepository.findById(id);
        UserStatus status = userStatusRepository.findById(user.getId());
        UserInformationDto dto = new UserInformationDto(user.getId(), user.getName(), user.getEmail(), user.getProfileImageId(), status.isOnline());

        return dto;
    }

    @Override
    public UserInformationDto update(UserUpdateDto dto) {
        User user = userRepository.findById(dto.id());

        boolean isDuplicated = findAll().stream()
                // 새로 받은 정보가 나를 제외하고, 다른 객체의 이름 및 이메일과 다른지 검시
                .filter(u -> !u.id().equals(dto.id()))
                .anyMatch(u -> u.name().equals(dto.name())
                        || u.email().equals(dto.email()));
        if (isDuplicated) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임/이메일입니다.");
        }

        UUID newProfileImageId = user.getProfileImageId();
        if (dto.profileImage() != null) {
            BinaryContent image = new BinaryContent(dto.profileImage());
            binaryContentRepository.save(image);
            newProfileImageId = image.getId();
        }
        // 일반 User 객체도 update 해주어야 함
        user.update(dto.name(), dto.email(), dto.password(), newProfileImageId);
        userRepository.save(user);

        // findByUserId()를 사용해야 함
        UserStatus status = userStatusRepository.findByUserId(user.getId());

        UserInformationDto updatedDto = new UserInformationDto(user.getId(),
                user.getName(), user.getEmail(), newProfileImageId, status.isOnline());
        return updatedDto;
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id);
        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }
        UserStatus status = userStatusRepository.findByUserId(user.getId());
        userStatusRepository.delete(status.getId());
        userRepository.delete(user.getId());
    }
}
