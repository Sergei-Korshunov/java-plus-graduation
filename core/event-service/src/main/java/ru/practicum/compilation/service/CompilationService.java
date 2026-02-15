package ru.practicum.compilation.service;

import ru.practicum.interactionapi.event.compilation.dto.CompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.UpdateCompilationDTO;

import java.util.List;

public interface CompilationService {

    CompilationDTO addCompilation(RequestToCreateNewCompilationDTO newCompilationDTO);

    CompilationDTO updateCompilation(Long compId, UpdateCompilationDTO updateCompilationDTO);

    List<CompilationDTO> getCompilationsList(Boolean pinned, Integer from, Integer size);

    CompilationDTO getCompilationById(Long compId);

    void removeCompilation(Long compId);

}