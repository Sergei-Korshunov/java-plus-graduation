package ru.practicum.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.interactionapi.event.compilation.dto.CompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.interactionapi.event.compilation.dto.UpdateCompilationDTO;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(RequestToCreateNewCompilationDTO newCompilationDTO);

    CompilationDTO toCompilationDto(Compilation compilation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    void updateCompilation(UpdateCompilationDTO updateCompilationDTO, @MappingTarget Compilation compilation);

}