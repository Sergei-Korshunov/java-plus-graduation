package ru.practicum.interactionapi.rating.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.interactionapi.rating.model.ReactionType;

@Data
public class RateRequest {

    @NotNull
    private ReactionType reaction;
}