package ru.practicum.avro;

import ru.practicum.avro.deserializer.BaseDeserializerAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionDeserializer extends BaseDeserializerAvro<UserActionAvro> {

    public UserActionDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}