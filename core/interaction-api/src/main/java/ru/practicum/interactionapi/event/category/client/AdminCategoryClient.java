package ru.practicum.interactionapi.event.category.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.event.category.dto.CategoryDto;
import ru.practicum.interactionapi.event.category.dto.NewCategoryDto;

@FeignClient(name = "event-service", path = "/admin/categories")
public interface AdminCategoryClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto addCategory(@Valid @RequestBody NewCategoryDto dto) throws FeignException;

    @PatchMapping("/{catId}")
    CategoryDto updateCategory(@PathVariable long catId,
                               @Valid @RequestBody CategoryDto dto) throws FeignException;

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable long catId) throws FeignException;
}