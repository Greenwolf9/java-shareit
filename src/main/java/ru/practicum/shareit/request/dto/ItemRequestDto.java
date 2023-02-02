package ru.practicum.shareit.request.dto;

import lombok.Value;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@Value
public class ItemRequestDto {

    long requestId;
    String requestDescr;
    long userId;
    Instant createdAt;
}
