package ru.practicum.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.collector.kafka.KafkaClient;
import ru.practicum.collector.mapper.UserActionMapper;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.UserActionProto;

@Slf4j
@Component
public class UserActionServiceImpl implements UserActionService {

    private final KafkaClient kafkaClient;
    private final UserActionMapper userActionMapper;

    private String topicName;

    @Autowired
    public UserActionServiceImpl(KafkaClient kafkaClient, UserActionMapper userActionMapper) {
        this.kafkaClient = kafkaClient;
        this.userActionMapper = userActionMapper;
    }

    @Override
    public void push(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = userActionMapper.toUserActionAvro(userActionProto);

        String topicName = kafkaClient.getKafkaProperty().getTopicUserAction();
        long timestamp = userActionAvro.getTimestamp().toEpochMilli();

        kafkaClient.sendData(topicName, null, timestamp, userActionAvro.getEventId(), userActionAvro);
    }
}