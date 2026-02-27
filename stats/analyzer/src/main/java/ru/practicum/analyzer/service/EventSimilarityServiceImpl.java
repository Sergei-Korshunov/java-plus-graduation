package ru.practicum.analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.mapper.EventSimilarityMapper;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.service.interfaces.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

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
            eventSimilarityRepository.save(eventSimilarityMapper.toEventSimilarity(eventSimilarityAvro));
        } else {
            EventSimilarity oldEventSimilarity = eventSimilarityRepository.findByEventAAndEventB(eventA, eventB);
            oldEventSimilarity.setScore(eventSimilarityAvro.getScore());
            oldEventSimilarity.setTimestamp(eventSimilarityAvro.getTimestamp());
        }
    }
}