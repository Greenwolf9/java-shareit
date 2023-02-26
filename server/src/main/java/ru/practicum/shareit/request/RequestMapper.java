package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemDetailsForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ItemRequestDetails toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDetailsForRequest> list = new ArrayList<>();
        return new ItemRequestDetails(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(), list);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(itemRequestDto.getRequestor())
                .created(LocalDateTime.now()).build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor(), itemRequest.getCreated());
    }

    public static List<ItemRequestDetails> toItemRequestList(Collection<ItemRequest> requests) {
        return requests.stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
