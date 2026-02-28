package ru.practicum.request.controller;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.request.client.PrivateRequestClient;
import ru.practicum.interactionapi.request.dto.RequestDTO;
import ru.practicum.interactionapi.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/request")
public class PrivateRequestController implements PrivateRequestClient {

    private final RequestService requestService;

    @Autowired
    public PrivateRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/{ids}")
    @Override
    public List<RequestDTO> findRequestsByIds(List<Long> ids) {
        return requestService.findRequestsByIds(ids);
    }

    @GetMapping("/event/{eventId}")
    @Override
    public List<RequestDTO> getRequestByEventId(Long eventId) {
        return requestService.getRequestByEventId(eventId);
    }

    @PostMapping("/save")
    @Override
    public void saveRequestList(List<RequestDTO> requestList) {
        requestService.saveRequestList(requestList);
    }

    @GetMapping("/{eventId}/userAttended/{userId}")
    @Override
    public boolean isUserAttendedEvent(@PositiveOrZero @PathVariable Long eventId,
                                       @PositiveOrZero @PathVariable Long userId,
                                       @RequestParam RequestStatus requestStatus) {
        return requestService.isUserAttendedEvent(eventId, userId, requestStatus);
    }
}