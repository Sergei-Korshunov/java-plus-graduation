package ru.practicum.client.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimeMapper {

    public Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public Timestamp toTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}