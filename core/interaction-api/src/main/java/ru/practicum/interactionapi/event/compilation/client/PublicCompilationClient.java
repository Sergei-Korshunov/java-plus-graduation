package ru.practicum.interactionapi.event.compilation.client;

import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.interactionapi.event.compilation.dto.CompilationDTO;

import java.util.List;

@FeignClient(name = "event-service", path = "/compilations")
public interface PublicCompilationClient {

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    List<CompilationDTO> getCompilationsList(@RequestParam(defaultValue = "false") Boolean pinned,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) throws FeignException;

    @GetMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    CompilationDTO getCompilationById(@PathVariable @NotNull @Positive Long compId) throws FeignException;
}