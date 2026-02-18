package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.interactionapi.event.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.event.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.request.client.PrivateRequestClient;
import ru.practicum.interactionapi.request.dto.RequestDTO;
import ru.practicum.interactionapi.request.model.RequestStatus;
import ru.practicum.interactionapi.user.client.UserClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EventWithRequestImpl implements EventWithRequest {

    private final EventService eventService;
    private final UserClient userClient;
    private final PrivateRequestClient privateRequestClient;

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestUser(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request) {
        userClient.getUserById(userId);
        Event event = eventService.findEventWithOutDto(userId, eventId);

        List<RequestDTO> requestList = privateRequestClient.findRequestsByIds(request.getRequestIds());
        List<RequestDTO> confirmedRequests = new ArrayList<>();
        List<RequestDTO> rejectedRequests = new ArrayList<>();

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit()) &&
                request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("У события достигнут лимит запросов на участие.");
        }

        for (RequestDTO req : requestList) {
            if (!req.getRequestStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Заявку c Id: " + req.getId() +
                        " можно одобрить, если у нее статус: " + RequestStatus.PENDING);
            }
        }

        if ((!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) && request.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (RequestDTO req : requestList) {
                req.setRequestStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(req);
            }
        } else if ((!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) && request.getStatus().equals(RequestStatus.REJECTED)) {
            for (RequestDTO req : requestList) {
                req.setRequestStatus(RequestStatus.REJECTED);
                rejectedRequests.add(req);
            }
        } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
            for (RequestDTO req : requestList) {
                req.setRequestStatus(RequestStatus.REJECTED);
                rejectedRequests.add(req);
            }
        } else {
            for (RequestDTO req : requestList) {
                if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
                    req.setRequestStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(req);
                } else {
                    req.setRequestStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    confirmedRequests.add(req);
                }
            }
        }

        List<RequestDTO> allRequest = new ArrayList<>();
        allRequest.addAll(confirmedRequests);
        allRequest.addAll(rejectedRequests);

        privateRequestClient.saveRequestList(allRequest);
        eventService.saveEventWithRequest(event);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    public List<RequestDTO> getEventRequest(Long userId, Long eventId) {
        userClient.getUserById(userId);
        eventService.findEventWithOutDto(userId, eventId);

        return privateRequestClient.getRequestByEventId(eventId);
    }
}