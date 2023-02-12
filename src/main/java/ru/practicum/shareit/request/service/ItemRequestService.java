package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDetails saveItemRequest(Long userId, ItemRequestDto itemRequestDto) throws ValidationException, NotFoundException;

    ItemRequestDetails getItemRequestById(Long requestId, Long userId) throws NotFoundException;

    List<ItemRequestDetails> getAllItemRequestsByUserId(Long userId) throws NotFoundException;

    List<ItemRequestDetails> getAllRequestsSorted(Long userId, int from, int size) throws ValidationException, NotFoundException;

    void deleteItemRequest(Long requestId);
}
