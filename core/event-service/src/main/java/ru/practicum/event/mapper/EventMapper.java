package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.interactionapi.event.event.dto.EventFullDto;
import ru.practicum.interactionapi.event.event.dto.EventShortDto;
import ru.practicum.interactionapi.event.event.dto.NewEventDto;
import ru.practicum.interactionapi.event.event.dto.UpdateEventUserRequest;
import ru.practicum.interactionapi.event.event.status.StateEvent;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @BeanMapping(qualifiedByName = "event")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "initiatorId")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "location", target = "location")
    Event toEvent(NewEventDto newEventDto, Long userId, Category category, Location location);

    @Named("event")
    @AfterMapping
    default void setDefaultCreatedOn(@MappingTarget Event.EventBuilder event) {
        event.createdOn(LocalDateTime.now());
        event.state(StateEvent.PENDING);
        event.confirmedRequests(0L);
        event.views(0L);
    }

    @Mapping(source = "event.initiatorId", target = "initiator.id")
    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, qualifiedByName = "updateEvent")
    @Mapping(target = "category", ignore = true)
    void toUpdateEvent(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);
}