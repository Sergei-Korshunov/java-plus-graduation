package ru.korshunov.statsclient;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import statsdto.HitDto;
import statsdto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "${discovery.services.stats-server-id}")
public interface StatsClient {

    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "addHitFallback")
    @PostMapping("/hit")
    void addHit(@Valid HitDto hitDto) throws FeignException;

    @CircuitBreaker(name = "defaultCircuitBreaker", fallbackMethod = "getStatsFallback")
    @GetMapping("/stats")
    List<StatDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) throws FeignException;

    default void addHitFallback(HitDto hitDto, Throwable throwable) {}

    default List<StatDto> getStatsFallback(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return List.of();
    }
}