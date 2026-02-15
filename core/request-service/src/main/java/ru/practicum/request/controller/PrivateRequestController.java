package ru.practicum.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interactionapi.request.client.PrivateRequestClient;
import ru.practicum.interactionapi.request.dto.RequestDTO;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/request")
public class PrivateRequestController implements PrivateRequestClient {
    private RequestService requestService;

    @Autowired
    public PrivateRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public List<RequestDTO> findRequestsByIds(List<Long> ids) {
        return requestService.findRequestsByIds(ids);
    }

    @Override
    public List<RequestDTO> getRequestByEventId(Long eventId) {
        return requestService.getRequestByEventId(eventId);
    }

    @Override
    public void saveRequestList(List<RequestDTO> requestList) {
        requestService.saveRequestList(requestList);
    }
}