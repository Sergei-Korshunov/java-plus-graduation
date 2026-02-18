package ru.practicum.interactionapi.event.event.client;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.event.event.dto.*;
import ru.practicum.interactionapi.request.dto.RequestDTO;

import java.util.List;

@FeignClient(name = "event-service", path = "/users/{userId}/events")
public interface PrivateEventClient {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    EventFullDto createEvent(@PathVariable @Positive @NotNull Long userId,
                             @RequestBody @Valid NewEventDto newEventDto) throws FeignException;

    @GetMapping
    List<EventShortDto> findEventsByUserId(@PathVariable @Positive @NotNull Long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) throws FeignException;

    @GetMapping("/{eventId}")
    EventFullDto findEventById(@PathVariable @Positive @NotNull Long userId,
                               @PathVariable @Positive @NotNull Long eventId) throws FeignException;

    @PatchMapping("/{eventId}")
    EventFullDto updateEvent(@PathVariable @Positive @NotNull Long userId,
                             @PathVariable @Positive @NotNull Long eventId,
                             @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) throws FeignException;

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResult updateRequestUser(@PathVariable @Positive @NotNull Long userId,
                                                     @PathVariable @Positive @NotNull Long eventId,
                                                     @RequestBody @Valid EventRequestStatusUpdateRequest request) throws FeignException;

    @GetMapping("/{eventId}/requests")
    List<RequestDTO> getEventRequest(@PathVariable @Positive @NotNull Long userId,
                                     @PathVariable @Positive @NotNull Long eventId) throws FeignException;
}