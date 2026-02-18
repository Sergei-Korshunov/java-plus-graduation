package ru.practicum.event.service;

import ru.practicum.interactionapi.event.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.event.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.request.dto.RequestDTO;

import java.util.List;

public interface EventWithRequest {

    EventRequestStatusUpdateResult updateRequestUser(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<RequestDTO> getEventRequest(Long userId, Long eventId);
}