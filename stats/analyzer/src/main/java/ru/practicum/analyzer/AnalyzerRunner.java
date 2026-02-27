package ru.practicum.analyzer;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.processor.Startable;

@Slf4j
@Component
public class AnalyzerRunner implements CommandLineRunner {

    private final Startable eventsSimilartyProcessor;
    private final Startable userActionProcessor;

    @Autowired
    public AnalyzerRunner(Startable eventsSimilartyProcessor, Startable userActionProcessor) {
        this.eventsSimilartyProcessor = eventsSimilartyProcessor;
        this.userActionProcessor = userActionProcessor;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Запуск сервиса Analyzer");
        Thread hubProcessorThread = new Thread((Runnable) eventsSimilartyProcessor);
        hubProcessorThread.setName("HubEventProcessorThread");

        log.info("Запуск обработчика 'сходства событий'");
        hubProcessorThread.start();

        log.info("Запуск обработчика 'действия пользователя'");
        userActionProcessor.start();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Остановка сервиса Analyzer");

        try {
            eventsSimilartyProcessor.stop();
        } catch (Exception e) {
            log.warn("Не удалось корректно остановить обработчик 'сходства событий'", e);
        }

        try {
            userActionProcessor.stop();
        } catch (Exception e) {
            log.warn("Не удалось корректно остановить обработчик 'действия пользователя'", e);
        }
    }
}