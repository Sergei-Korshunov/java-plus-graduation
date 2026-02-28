package ru.practicum.analyzer.service.interfaces;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityService {

    void update(EventSimilarityAvro eventSimilarityAvro);
}