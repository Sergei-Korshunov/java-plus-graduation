package ru.practicum.rating.service;

import ru.practicum.interactionapi.rating.dto.RatingSummaryDto;
import ru.practicum.interactionapi.rating.model.ReactionType;

public interface RatingService {
    RatingSummaryDto rate(Long userId, Long eventId, ReactionType reaction);

    RatingSummaryDto removeRate(Long userId, Long eventId);

    RatingSummaryDto getSummary(Long eventId);
}