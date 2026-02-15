package ru.practicum.interactionapi.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interactionapi.request.dto.RequestDTO;

import java.util.List;

@FeignClient(name = "${discovery.services.private-request-service-id}", path = "/request")
public interface PrivateRequestClient {

    @GetMapping("/{ids}")
    List<RequestDTO> findRequestsByIds(@PathVariable List<Long> ids);

    @GetMapping("/event/{eventId}")
    List<RequestDTO> getRequestByEventId(@PathVariable Long eventId);

    @PostMapping("/save")
    void saveRequestList(@RequestBody List<RequestDTO> requestList);
}