package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.client.mapper.TimeMapper;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.util.Json;

import java.time.Instant;

@Slf4j
@Service
public class UserActionClient {

    private final UserActionControllerGrpc.UserActionControllerBlockingStub userActionStub;
    private final TimeMapper timeMapper;

    public UserActionClient(@GrpcClient("collector") UserActionControllerGrpc.UserActionControllerBlockingStub client,
                            TimeMapper timeMapper) {
        this.userActionStub = client;
        this.timeMapper = timeMapper;
    }

    public void collectUserAction(Long eventId, Long userId, ActionTypeProto type, Instant instant) {
        UserActionProto request = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setActionType(type)
                .setTimestamp(timeMapper.toTimestamp(instant))
                .build();

        log.info("Создан запрос UserAction: {}", Json.protoObjectToJson(request));
        userActionStub.collectUserAction(request);
    }
}