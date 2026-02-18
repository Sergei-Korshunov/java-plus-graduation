package ru.practicum.interactionapi.event.event.client;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.event.event.dto.EventFullDto;
import ru.practicum.interactionapi.event.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "event-service", path = "/events")
public interface PublicEventClient {

    @GetMapping
    List<EventShortDto> findEventByParamsPublic(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                @RequestParam(required = false) String sort,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest request) throws FeignException;

    @GetMapping("/{eventId}")
    EventFullDto findPublicEventById(@PathVariable @Positive @NotNull Long eventId,
                                     HttpServletRequest request) throws FeignException;
}