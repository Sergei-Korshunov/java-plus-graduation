package ru.practicum.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.event.event.client.AdminEventClient;
import ru.practicum.interactionapi.event.event.dto.EventFullDto;
import ru.practicum.interactionapi.event.event.status.StateEvent;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.request.dto.RequestDTO;
import ru.practicum.interactionapi.request.model.RequestStatus;
import ru.practicum.interactionapi.user.client.UserClient;
import ru.practicum.interactionapi.user.dto.UserDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserClient userClient;
    private final AdminEventClient adminEventClient;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, RequestMapper requestMapper, UserClient userClient, AdminEventClient adminEventClient) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.userClient = userClient;
        this.adminEventClient = adminEventClient;
    }

    @Transactional
    @Override
    public RequestDTO addRequestCurrentUser(Long userId, Long eventId) {
        UserDto user = userClient.getUserById(userId);
        EventFullDto event = adminEventClient.getEventById(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new ConflictException("Данный запрос существует.");

        if (user.getId().equals(event.getInitiator().getId()))
            throw new ConflictException("Невозможно создать запрос на участие в своем же событии.");

        if (!event.getState().equals(StateEvent.PUBLISHED))
            throw new ConflictException("Нельзя участвовать в неопубликованном событии.");

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("У события достигнут лимит запросов на участие.");
        }

        Long confirmedRequests = event.getConfirmedRequests();

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequesterId(user.getId());
        request.setEventId(event.getId());

        if (!event.getRequestModeration()) {
            request.setRequestStatus(RequestStatus.CONFIRMED);
            confirmedRequests++;
        } else {
            if (event.getParticipantLimit() == 0) {
                request.setRequestStatus(RequestStatus.CONFIRMED);
                confirmedRequests++;
            } else {
                request.setRequestStatus(RequestStatus.PENDING);
            }
        }

        if(!event.getConfirmedRequests().equals(confirmedRequests)) {
            event.setConfirmedRequests(confirmedRequests);
            adminEventClient.updateConfirmedRequest(eventId, event);
        }

        return requestMapper.toRequestDTO(requestRepository.save(request));
    }

    @Override
    public List<RequestDTO> getRequestsCurrentUser(Long userId) {
        userClient.getUserById(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RequestDTO cancelRequestCurrentUser(Long userId, Long requestId) {
        userClient.getUserById(userId);
        getRequestById(requestId);

        Request requestFromDatabase = requestRepository.findByIdAndRequesterId(requestId, userId);
        requestFromDatabase.setRequestStatus(RequestStatus.CANCELED);

        Request savedRequest = requestRepository.save(requestFromDatabase);

        return requestMapper.toRequestDTO(savedRequest);
    }

    @Override
    public List<RequestDTO> findRequestsByIds(List<Long> requestIds) {
        List<Request> requests = requestRepository.findRequestsByIds(requestIds).orElseThrow(() ->
                new NotFoundException("Запросы(Request) с переданными ids: " + requestIds + " не найдены:"));

        return requests.stream().map(requestMapper::toRequestDTO).toList();
    }

    @Transactional
    @Override
    public void saveRequestList(List<RequestDTO> requestList) {
        requestRepository.saveAll(requestList.stream().map(requestMapper::toRequest).toList());
    }

    @Override
    public List<RequestDTO> getRequestByEventId(Long eventId) {
        List<Request> requestList = requestRepository.getRequestByEventId(eventId);

        return requestList.stream().map(requestMapper::toRequestDTO).toList();
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id - %d не найден.", requestId)));
    }
}