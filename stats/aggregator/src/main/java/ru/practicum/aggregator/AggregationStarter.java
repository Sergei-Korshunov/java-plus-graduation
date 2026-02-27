package ru.practicum.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.service.UserActionService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class AggregationStarter {
    private final KafkaConsumer<Long, UserActionAvro> consumer;

    private final KafkaProducer<Long, EventSimilarityAvro> producer;

    private final UserActionService userActionService;

    @Value("${aggregator.kafka.consumer.topic.user-action}")
    private String topicUserAction;

    @Value("${aggregator.kafka.producer.topic.events-similarity}")
    private String topicEventSimilarity;

    @Value("${aggregator.kafka.consumer.properties.poll-timeout}")
    private int pollTimeout;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Autowired
    public AggregationStarter(KafkaConsumer<Long, UserActionAvro> consumer,
                              KafkaProducer<Long, EventSimilarityAvro> producer, UserActionService userActionService) {
        this.consumer = consumer;
        this.producer = producer;
        this.userActionService = userActionService;
    }

    public void start() {
        try {
            consumer.subscribe(List.of(topicUserAction));
            log.info("Подписка на топик: {}", topicUserAction);
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));
                if (records.count() > 0)
                    log.info("Получено сообщений: {}", records.count());

                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro userActionAvro = record.value();
                    List<EventSimilarityAvro> result = userActionService.calculateSimilarity(userActionAvro);

                    log.info("Сообщений подготовлено к отправки: {}", result.size());

                    result.stream()
                            .map(eventSimilarityAvro ->
                                    new ProducerRecord<>(
                                            topicEventSimilarity,
                                            null,
                                            eventSimilarityAvro.getTimestamp().toEpochMilli(),
                                            eventSimilarityAvro))
                            .forEach(producerRecord -> {
                                log.info("Отправка");
                                producer.send(producerRecord);
                            });

                    fixOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    public void stop() {
        Optional.ofNullable(consumer).ifPresent(KafkaConsumer::wakeup);
    }

    private void fixOffsets(ConsumerRecord<Long, UserActionAvro> record, int count, KafkaConsumer<Long, UserActionAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}