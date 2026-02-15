package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interactionapi.request.dto.RequestDTO;
import ru.practicum.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "request.eventId", target = "eventId")
    @Mapping(source = "request.requesterId", target = "requesterId")
    RequestDTO toRequestDTO(Request request);

    Request toRequest(RequestDTO requestDTO);
}