package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Mapper(componentModel = "spring")
public interface UserActionMapper {

    UserAction toUserAction(UserActionAvro userActionAvro);
}