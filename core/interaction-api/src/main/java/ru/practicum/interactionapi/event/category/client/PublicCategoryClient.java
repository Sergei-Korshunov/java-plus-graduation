package ru.practicum.interactionapi.event.category.client;

import feign.FeignException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.event.category.dto.CategoryDto;

import java.util.List;

@FeignClient(name = "event-service", path = "/categories")
public interface PublicCategoryClient {

    @GetMapping
    List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) throws FeignException;

    @GetMapping("/{catId}")
    CategoryDto getCategory(@PathVariable long catId) throws FeignException;
}