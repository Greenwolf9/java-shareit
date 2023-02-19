package ru.practicum.shareit.request.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
public class ItemDetailsForRequest {

    Long id;
    String name;
    @NotNull
    @NotEmpty(message = "Description shouldn't be empty")
    @NotBlank
    String description;
    Boolean available;
    Long requestId;

}
