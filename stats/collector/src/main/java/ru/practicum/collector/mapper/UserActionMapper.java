package ru.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Component
public class UserActionMapper {

    public UserActionAvro toUserActionAvro(UserActionProto userActionProto) {
        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(toActionTypeAvro(userActionProto.getActionType()))
                .setTimestamp(toInstant(userActionProto.getTimestamp()))
                .build();
    }

    public ActionTypeAvro toActionTypeAvro(ActionTypeProto actionTypeProto) {
        switch (actionTypeProto) {
            case ACTION_VIEW: return ActionTypeAvro.VIEW;
            case ACTION_REGISTER: return ActionTypeAvro.REGISTER;
            case ACTION_LIKE: return ActionTypeAvro.LIKE;
        }

        return null;
    }

    public Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}