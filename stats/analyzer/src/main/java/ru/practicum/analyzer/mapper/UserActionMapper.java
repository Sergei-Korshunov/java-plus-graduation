package ru.practicum.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
public class UserActionMapper {

    public UserAction toUserAction(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .eventId(userActionAvro.getEventId())
                .userId(userActionAvro.getUserId())
                .mark(toMark(userActionAvro.getActionType()))
                .timestamp(userActionAvro.getTimestamp())
                .build();
    }

    private Double toMark(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}