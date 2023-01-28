package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getRequestId(),
                itemRequest.getRequestDescr(),
                itemRequest.getRequestId(),
                itemRequest.getCreatedAt());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getRequestId(),
                itemRequestDto.getRequestDescr(),
                itemRequestDto.getRequestId(),
                itemRequestDto.getCreatedAt());
    }

    public static List<ItemRequestDto> toItemRequestList(Collection<ItemRequest> requests) {
        return requests.stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
