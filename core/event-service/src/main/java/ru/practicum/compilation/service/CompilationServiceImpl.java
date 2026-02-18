package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.interactionapi.event.compilation.dto.CompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.UpdateCompilationDTO;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.util.PageRequestUtil;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationMapper compilationMapper, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Override
    public CompilationDTO addCompilation(RequestToCreateNewCompilationDTO newCompilationDTO) {
        if (newCompilationDTO.getPinned() == null) {
            newCompilationDTO.setPinned(false);
        }
        List<Event> events = new ArrayList<>();
        if (newCompilationDTO.getEvents() != null && !newCompilationDTO.getEvents().isEmpty()) {
            events = eventRepository.findEventsByIds(newCompilationDTO.getEvents());
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDTO);
        compilation.setEvents(events);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public CompilationDTO updateCompilation(Long compId, UpdateCompilationDTO updateCompilationDTO) {
        Compilation foundCompilation = findCompilationById(compId);
        if (updateCompilationDTO.getEvents() != null && !updateCompilationDTO.getEvents().isEmpty()) {
            foundCompilation.setEvents(eventRepository.findEventsByIds(updateCompilationDTO.getEvents()));
        }
        compilationMapper.updateCompilation(updateCompilationDTO, foundCompilation);
        return compilationMapper.toCompilationDto(foundCompilation);
    }

    private Compilation findCompilationById(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Подборка с указанным id - %d не найдена.", compId)));
    }

    @Override
    public List<CompilationDTO> getCompilationsList(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequestUtil.of(from, size, Sort.by("id").ascending());
        return compilationRepository.findCompilationByPinned(pinned, pageable).stream()
                .map(compilationMapper::toCompilationDto)
                .toList();
    }

    @Override
    public CompilationDTO getCompilationById(Long compId) {
        return compilationMapper.toCompilationDto(findCompilationById(compId));
    }

    @Transactional
    @Override
    public void removeCompilation(Long compId) {
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }
}