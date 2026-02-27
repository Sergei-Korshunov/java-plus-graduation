package ru.practicum.aggregator;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AggregatorRunner implements CommandLineRunner {

    private final AggregationStarter aggregationStarter;

    @Autowired
    public AggregatorRunner(AggregationStarter aggregationStarter) {
        this.aggregationStarter = aggregationStarter;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Запуск сервиса Aggregator");
        aggregationStarter.start();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Остановка сервиса Aggregator");

        try {
            aggregationStarter.stop();
        } catch (Exception e) {
            log.warn("Не удалось корректно остановить агрегирование данных", e);
        }
    }
}