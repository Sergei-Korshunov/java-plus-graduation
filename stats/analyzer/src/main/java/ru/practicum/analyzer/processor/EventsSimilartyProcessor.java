package ru.practicum.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.interfaces.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class EventsSimilartyProcessor implements Runnable, Startable {

    private final Consumer<Long, EventSimilarityAvro> consumer;
    private final EventSimilarityService eventSimilarityService;

    @Value("${analyzer.kafka.consumer.topics.events-similarity}")
    private String topicEventSimilarity;

    @Value("${analyzer.kafka.consumer.events-similarity.properties.poll-timeout}")
    private int pollTimeout;

    @Autowired
    public EventsSimilartyProcessor(Consumer<Long, EventSimilarityAvro> consumer, EventSimilarityService eventSimilarityService) {
        this.consumer = consumer;
        this.eventSimilarityService = eventSimilarityService;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
        try {
            log.info("Подписка на топик(EventsSimilarty): {}", topicEventSimilarity);
            consumer.subscribe(List.of(topicEventSimilarity));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<Long, EventSimilarityAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));
                if (records.count() > 0)
                    log.info("Получено сообщений: {}", records.count());

                for (ConsumerRecord<Long, EventSimilarityAvro> record : records) {
                    EventSimilarityAvro eventSimilarity = record.value();
                    log.info("Получен коэффициент схожести: {}", eventSimilarity);

                    eventSimilarityService.update(eventSimilarity);
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topicEventSimilarity);
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