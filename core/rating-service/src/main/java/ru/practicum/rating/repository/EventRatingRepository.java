package ru.practicum.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.rating.model.EventRating;

import java.util.Optional;

@Repository
public interface EventRatingRepository extends JpaRepository<EventRating, Long> {
    Optional<EventRating> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("select coalesce(sum(er.value), 0) from EventRating er where er.eventId = ?1")
    Long sumScoreByEventId(Long eventId);

    @Query("select count(er) from EventRating er where er.eventId = ?1 and er.value = 1")
    Long countLikes(Long eventId);

    @Query("select count(er) from EventRating er where er.eventId = ?1 and er.value = -1")
    Long countDislikes(Long eventId);
}