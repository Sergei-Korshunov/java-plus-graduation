package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.interactionapi.user.dto.NewUserRequest;
import ru.practicum.interactionapi.user.dto.UserDto;
import ru.practicum.interactionapi.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(NewUserRequest d);

    UserDto toDto(User u);

    UserShortDto toUserShortDto(User user);
}