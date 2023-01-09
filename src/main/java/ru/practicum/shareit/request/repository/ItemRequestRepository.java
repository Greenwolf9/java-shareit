package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository {

    ItemRequest getItemRequestById(long requestId);

    List<ItemRequest> getAllItemRequestsByUserId(long userId);

    ItemRequest saveItemRequest(ItemRequest itemRequest);

    ItemRequest updateItemRequest(long requestId);

    ItemRequest deleteItemRequest(long requestId);
}
