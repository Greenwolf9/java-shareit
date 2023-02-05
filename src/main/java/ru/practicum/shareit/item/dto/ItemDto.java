package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {

    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest itemRequest;
}
