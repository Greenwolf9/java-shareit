package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest getItemRequestById(long requestId);

    List<ItemRequest> getAllItemRequestsByUserId(long userId);

    ItemRequest saveItemRequest(ItemRequest itemRequest);

    ItemRequest updateItemRequest(long requestId);

    ItemRequest deleteItemRequest(long requestId);
}
