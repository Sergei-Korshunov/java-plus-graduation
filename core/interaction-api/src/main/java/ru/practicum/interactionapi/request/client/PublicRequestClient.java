package ru.practicum.interactionapi.request.client;

import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.request.dto.RequestDTO;

import java.util.List;

@FeignClient(name = "request-service", path = "/users/{userId}/requests")
public interface PublicRequestClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDTO addRequestCurrentUser(@Positive @PathVariable Long userId,
                                     @RequestParam @NotNull @Positive Long eventId) throws FeignException;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<RequestDTO> getRequestsCurrentUser(@Positive @PathVariable Long userId) throws FeignException;

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    RequestDTO cancelRequestCurrentUser(@Positive @PathVariable Long userId,
                                        @Positive @PathVariable Long requestId) throws FeignException;
}