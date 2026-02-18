package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.interactionapi.event.compilation.client.AdminCompilationClient;
import ru.practicum.interactionapi.event.compilation.dto.CompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.UpdateCompilationDTO;

import java.util.Objects;

@RestController
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController implements AdminCompilationClient {

    private final CompilationService compilationService;

    @Autowired
    public AdminCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Override
    public CompilationDTO addCompilation(@Valid @RequestBody RequestToCreateNewCompilationDTO newCompilationDTO) {
        return compilationService.addCompilation(newCompilationDTO);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Override
    public CompilationDTO updateCompilation(
            @Positive @PathVariable Long compId,
            @Valid @RequestBody(required = false) UpdateCompilationDTO updateCompilationDTO) {
        return compilationService.updateCompilation(compId,
                Objects.requireNonNullElseGet(updateCompilationDTO, UpdateCompilationDTO::new));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Override
    public void removeCompilation(@Positive @PathVariable Long compId) {
        compilationService.removeCompilation(compId);
    }
}