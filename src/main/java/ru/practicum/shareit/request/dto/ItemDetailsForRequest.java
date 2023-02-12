package ru.practicum.shareit.request.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ItemDetailsForRequest {
    @NotNull
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;

}
