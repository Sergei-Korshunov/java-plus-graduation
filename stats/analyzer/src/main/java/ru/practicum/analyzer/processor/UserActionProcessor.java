package ru.practicum.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.interfaces.UserActionService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserActionProcessor implements Startable {

    private final Consumer<Long, UserActionAvro> consumer;
    private final UserActionService userActionService;

    @Value("${analyzer.kafka.consumer.topics.user-action}")
    private String topicUserAction;

    @Value("${analyzer.kafka.consumer.user-action.properties.poll-timeout}")
    private int pollTimeout;

    public UserActionProcessor(Consumer<Long, UserActionAvro> consumer, UserActionService userActionService) {
        this.consumer = consumer;
        this.userActionService = userActionService;
    }

    @Override
    public void start() {
        try {
            log.info("Подписка на топик(UserAction): {}", topicUserAction);
            consumer.subscribe(List.of(topicUserAction));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro action = record.value();
                    log.info("Получено действие пользователя {}", action);

                    userActionService.update(action);
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topicUserAction);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    @Override
    public void stop() {
        Optional.ofNullable(consumer).ifPresent(Consumer::wakeup);
    }
}