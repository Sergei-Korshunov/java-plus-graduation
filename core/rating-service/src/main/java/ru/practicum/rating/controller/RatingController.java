package ru.practicum.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.rating.client.RatingClient;
import ru.practicum.interactionapi.rating.dto.RateRequest;
import ru.practicum.interactionapi.rating.dto.RatingSummaryDto;
import ru.practicum.rating.service.RatingService;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class RatingController implements RatingClient {

    private final RatingService ratingService;

    @PostMapping("/users/{userId}/events/{eventId}/rating")
    @Override
    public RatingSummaryDto rate(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody @Valid RateRequest request) {
        return ratingService.rate(userId, eventId, request.getReaction());
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/rating")
    @Override
    public RatingSummaryDto remove(@PathVariable Long userId, @PathVariable Long eventId) {
        return ratingService.removeRate(userId, eventId);
    }

    @GetMapping("/events/{eventId}/rating")
    @Override
    public RatingSummaryDto get(@PathVariable Long eventId) {
        return ratingService.getSummary(eventId);
    }
}