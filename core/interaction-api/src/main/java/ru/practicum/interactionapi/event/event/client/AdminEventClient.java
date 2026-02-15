package ru.practicum.interactionapi.event.event.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.event.event.dto.EventFullDto;
import ru.practicum.interactionapi.event.event.dto.UpdateEventAdminRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "${discovery.services.admin-event-service-id}", path = "/admin/events")
public interface AdminEventClient {

    @PatchMapping("/{eventId}")
    EventFullDto updateEventAdmin(@PathVariable @Positive @NotNull Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto);

    @PatchMapping("/update/confirmedRequests/{eventId}")
    void updateConfirmedRequest(@PathVariable @Positive @NotNull Long eventId,
                                  @Valid @RequestBody EventFullDto eventFullDto);

    @GetMapping("/{eventId}")
    EventFullDto getEventById(@PathVariable @Positive Long eventId);

    @GetMapping
    List<EventFullDto> findEventByParamsAdmin(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<String> state,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size);
}