package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.mapper.EventSimilarityMapper;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.service.interfaces.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.util.Json;

@Slf4j
@Service
public class EventSimilarityServiceImpl implements EventSimilarityService {

    private final EventSimilarityRepository eventSimilarityRepository;
    private final EventSimilarityMapper eventSimilarityMapper;

    @Autowired
    public EventSimilarityServiceImpl(EventSimilarityRepository eventSimilarityRepository, EventSimilarityMapper eventSimilarityMapper) {
        this.eventSimilarityRepository = eventSimilarityRepository;
        this.eventSimilarityMapper = eventSimilarityMapper;
    }

    @Transactional
    @Override
    public void update(EventSimilarityAvro eventSimilarityAvro) {
        Long eventA = eventSimilarityAvro.getEventA();
        Long eventB = eventSimilarityAvro.getEventB();

        if (!eventSimilarityRepository.existsByEventAAndEventB(eventA, eventB)) {
            EventSimilarity eventSimilarity = eventSimilarityMapper.toEventSimilarity(eventSimilarityAvro);

            log.info("Сходство событий сохранены с новыми данными {}", Json.simpleObjectToJson(eventSimilarity));
            eventSimilarityRepository.save(eventSimilarity);
        } else {
            EventSimilarity oldEventSimilarity = eventSimilarityRepository.findByEventAAndEventB(eventA, eventB);
            oldEventSimilarity.setScore(eventSimilarityAvro.getScore());
            oldEventSimilarity.setTimestamp(eventSimilarityAvro.getTimestamp());

            log.info("Сходство событий дополнены новыми данными {}", Json.simpleObjectToJson(oldEventSimilarity));
            eventSimilarityRepository.save(oldEventSimilarity);
        }
    }
}