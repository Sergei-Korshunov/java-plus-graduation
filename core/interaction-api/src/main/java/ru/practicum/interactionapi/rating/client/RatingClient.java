package ru.practicum.interactionapi.rating.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.rating.dto.RateRequest;
import ru.practicum.interactionapi.rating.dto.RatingSummaryDto;

@FeignClient(name = "rating-service")
public interface RatingClient {

    @PostMapping("/users/{userId}/events/{eventId}/rating")
    RatingSummaryDto rate(@PathVariable Long userId,
                          @PathVariable Long eventId,
                          @RequestBody @Valid RateRequest request) throws FeignException;

    @DeleteMapping("/users/{userId}/events/{eventId}/rating")
    RatingSummaryDto remove(@PathVariable Long userId, @PathVariable Long eventId) throws FeignException;

    @GetMapping("/events/{eventId}/rating")
    RatingSummaryDto get(@PathVariable Long eventId) throws FeignException;
}