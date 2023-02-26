package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.user.model.User;

@Value
public class ItemDto {

    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    Long requestId;
}
