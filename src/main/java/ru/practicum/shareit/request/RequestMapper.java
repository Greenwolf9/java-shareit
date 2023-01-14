package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getRequestId(),
                itemRequest.getRequestDescr(),
                itemRequest.getRequestedBy().getId(),
                itemRequest.getCreatedAt());
    }
}
