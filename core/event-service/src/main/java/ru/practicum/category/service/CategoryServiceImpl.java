package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.interactionapi.event.category.dto.CategoryDto;
import ru.practicum.interactionapi.event.category.dto.NewCategoryDto;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.util.PageRequestUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto dto) {
        if (repository.existsByNameIgnoreCase(dto.getName()))
            throw new ConflictException("Category name already exists: " + dto.getName());
        Category saved = repository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long id, CategoryDto dto) {
        Category cat = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        if (repository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id))
            throw new ConflictException("Category name already exists: " + dto.getName());
        cat.setName(dto.getName());
        return categoryMapper.toDto(repository.save(cat));
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        if (!repository.existsById(id)) throw new NotFoundException("Category with id=" + id + " was not found");
        // В будущем здесь проверим привязанные события и вернём 409
        repository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        var pr = PageRequestUtil.of(from, size, Sort.by("id").ascending());
        return repository.findAll(pr).map(categoryMapper::toDto).getContent();
    }

    //Поиск категории
    @Override
    public CategoryDto getCategory(long id) {
        return repository.findById(id).map(categoryMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }
}

