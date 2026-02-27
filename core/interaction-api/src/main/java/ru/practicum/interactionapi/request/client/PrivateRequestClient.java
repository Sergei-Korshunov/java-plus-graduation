package ru.practicum.interactionapi.request.client;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.request.dto.RequestDTO;
import ru.practicum.interactionapi.request.model.RequestStatus;

import java.util.List;

@FeignClient(name = "${discovery.services.private-request-service-id}", path = "/request")
public interface PrivateRequestClient {

    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "findRequestsByIdsFallback")
    @GetMapping("/{ids}")
    List<RequestDTO> findRequestsByIds(@PathVariable List<Long> ids) throws FeignException;

    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "getRequestByEventIdFallback")
    @GetMapping("/event/{eventId}")
    List<RequestDTO> getRequestByEventId(@PathVariable Long eventId) throws FeignException;

    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "saveRequestListFallback")
    @PostMapping("/save")
    void saveRequestList(@RequestBody List<RequestDTO> requestList) throws FeignException;

    @GetMapping("/{eventId}/userAttended/{userId}")
    boolean isUserAttendedEvent(@PositiveOrZero @PathVariable Long eventId,
                                @PositiveOrZero @PathVariable Long userId,
                                @RequestParam RequestStatus requestStatus) throws FeignException;

    default List<RequestDTO> findRequestsByIdsFallback(List<Long> ids, Throwable throwable) {
        return List.of();
    }

    default List<RequestDTO> getRequestByEventIdFallback(Long eventId, Throwable throwable) {
        return List.of();
    }

    default void saveRequestListFallback(List<RequestDTO> requestList, Throwable throwable) {

    }
}