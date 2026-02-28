package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.mapper.UserActionMapper;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.analyzer.service.interfaces.UserActionService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.util.Json;

@Slf4j
@Service
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;
    private final UserActionMapper userActionMapper;

    @Value("${analyzer.weights.view}")
    private double view;

    @Value("${analyzer.weights.register}")
    private double register;

    @Value("${analyzer.weights.like}")
    private double like;

    @Autowired
    public UserActionServiceImpl(UserActionRepository userActionRepository, UserActionMapper userActionMapper) {
        this.userActionRepository = userActionRepository;
        this.userActionMapper = userActionMapper;
    }

    @Override
    public void update(UserActionAvro userActionAvro) {
        Long eventId = userActionAvro.getEventId();
        Long userId = userActionAvro.getUserId();
        Double newMark = getMark(userActionAvro);

        if (!userActionRepository.existsByEventIdAndUserId(eventId, userId)) {
            UserAction userAction = userActionMapper.toUserAction(userActionAvro);

            log.info("Действие пользователя сохранены с новыми данными {}", Json.simpleObjectToJson(userAction));
            userActionRepository.save(userAction);
        } else {
            UserAction userAction = userActionRepository.findByEventIdAndUserId(eventId, userId);
            if (userAction.getMark() < newMark) {
                userAction.setMark(newMark);
                userAction.setTimestamp(userActionAvro.getTimestamp());

                log.info("Действие пользователя дополнены новыми данными {}", Json.simpleObjectToJson(userAction));
                userActionRepository.save(userAction);
            }
        }
    }

    private Double getMark(UserActionAvro userActionAvro) {
        return switch (userActionAvro.getActionType()) {
            case LIKE -> like;
            case REGISTER -> register;
            case VIEW -> view;
        };
    }
}