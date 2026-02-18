package ru.practicum.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.event.event.client.AdminEventClient;
import ru.practicum.interactionapi.event.event.dto.EventFullDto;
import ru.practicum.interactionapi.event.event.status.StateEvent;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.rating.dto.RatingSummaryDto;
import ru.practicum.interactionapi.rating.model.ReactionType;
import ru.practicum.interactionapi.user.client.UserClient;
import ru.practicum.rating.model.EventRating;
import ru.practicum.rating.repository.EventRatingRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

    private final EventRatingRepository ratingRepository;
    private final UserClient userClient;
    private final AdminEventClient adminEventClient;

    @Transactional
    @Override
    public RatingSummaryDto rate(Long userId, Long eventId, ReactionType reaction) {
        userClient.getUserById(userId);
        EventFullDto event = adminEventClient.getEventById(eventId);

        if (event.getInitiator() != null && event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может оценивать своё событие");
        }

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException("Нельзя голосовать за непубликованное событие");
        }

        EventRating rating = ratingRepository.findByEventIdAndUserId(eventId, userId)
                .orElseGet(() -> EventRating.builder()
                        .eventId(eventId)
                        .userId(userId)
                        .createdAt(LocalDateTime.now())
                        .build());

        rating.setReaction(reaction);
        rating.setValue(reaction.getScore());
        rating.setUpdatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
        return getSummary(eventId);
    }

    @Transactional
    @Override
    public RatingSummaryDto removeRate(Long userId, Long eventId) {
        EventRating rating = ratingRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Реакция не найдена"));
        ratingRepository.delete(rating);
        return getSummary(eventId);
    }

    @Override
    public RatingSummaryDto getSummary(Long eventId) {
        Long likes = ratingRepository.countLikes(eventId);
        Long dislikes = ratingRepository.countDislikes(eventId);
        Long total = ratingRepository.sumScoreByEventId(eventId);
        return RatingSummaryDto.builder()
                .eventId(eventId)
                .likes(likes)
                .dislikes(dislikes)
                .rating(total)
                .build();
    }
}