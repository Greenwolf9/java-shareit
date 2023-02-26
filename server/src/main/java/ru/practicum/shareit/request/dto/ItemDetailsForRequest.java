package ru.practicum.shareit.request.dto;

import lombok.Value;

@Value
public class ItemDetailsForRequest {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;

}
