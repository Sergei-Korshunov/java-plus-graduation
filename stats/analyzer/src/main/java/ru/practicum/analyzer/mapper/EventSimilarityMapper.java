package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Mapper(componentModel = "spring")
public interface EventSimilarityMapper {

    EventSimilarity toEventSimilarity(EventSimilarityAvro eventSimilarityAvro);
}