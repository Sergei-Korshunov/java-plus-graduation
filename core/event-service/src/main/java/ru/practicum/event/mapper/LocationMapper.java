package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.event.model.Location;
import ru.practicum.interactionapi.event.event.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toLocation(LocationDto location);

    LocationDto toLocationDto(Location location);
}