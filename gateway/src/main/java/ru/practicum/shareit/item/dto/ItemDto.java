package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {

    Long id;
    String name;
    String description;
    Boolean available;
    UserDto owner;
    Long requestId;
}
