package ru.practicum.interactionapi.event.compilation.client;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.event.compilation.dto.CompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.UpdateCompilationDTO;

@FeignClient(name = "event-service", path = "/admin/compilations")
public interface AdminCompilationClient {

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    CompilationDTO addCompilation(@Valid @RequestBody RequestToCreateNewCompilationDTO newCompilationDTO) throws FeignException;

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    CompilationDTO updateCompilation(@Positive @PathVariable Long compId,
                                     @Valid @RequestBody(required = false) UpdateCompilationDTO updateCompilationDTO) throws FeignException;

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void removeCompilation(@Positive @PathVariable Long compId) throws FeignException;
}