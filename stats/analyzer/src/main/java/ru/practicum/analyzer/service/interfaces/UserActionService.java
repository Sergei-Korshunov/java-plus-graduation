package ru.practicum.analyzer.service.interfaces;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionService {

    void update(UserActionAvro userActionAvro);
}