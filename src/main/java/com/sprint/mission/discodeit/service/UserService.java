package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserInformationDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateDto dto);
    List<UserInformationDto> findAll();
    UserInformationDto findById(UUID id);
    UserInformationDto update(UserUpdateDto dto);
    void delete(UUID id);
}
