package ru.practicum.user.service;

import ru.practicum.interactionapi.user.dto.NewUserRequest;
import ru.practicum.interactionapi.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest req);

    List<UserDto> get(List<Long> ids, int from, int size);

    void delete(long userId);

    UserDto getUserById(Long userId);
}