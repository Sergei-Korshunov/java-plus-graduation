package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.model.Category;
import ru.practicum.interactionapi.event.category.dto.CategoryDto;
import ru.practicum.interactionapi.event.category.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(NewCategoryDto dto);

    CategoryDto toDto(Category e);

    Category toCategory(CategoryDto categoryDto);
}