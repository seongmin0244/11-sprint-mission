package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> create(@RequestBody UserCreateDto dto) {
        User u = userService.create(dto);
        UserDto userDto = new UserDto(u.getId(), u.getCreatedAt(), u.getUpdatedAt(), u.getName(), u.getEmail(), u.getProfileImageId(), true);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<UserDto> update(@PathVariable UUID id,
                          @RequestBody UserUpdateDto dto) {
        User u = userService.update(id, dto);
        UserDto userDto = new UserDto(u.getId(), u.getCreatedAt(), u.getUpdatedAt(), u.getName(), u.getEmail(), u.getProfileImageId(), true);

        return ResponseEntity.ok(userDto);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userDtoList = userService.findAll();
        return ResponseEntity.ok(userDtoList);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateStatus(@PathVariable("id") UUID userId) {
        UserStatus userStatus = userStatusService.updateByUserId(userId);
        return ResponseEntity.ok(userStatus);
    }
}
