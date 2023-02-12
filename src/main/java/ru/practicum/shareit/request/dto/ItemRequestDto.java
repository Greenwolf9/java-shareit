package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Value
public class ItemRequestDto {

    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
