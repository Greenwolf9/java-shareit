package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItems();

    List<ItemDto> getAllItemsByUserId(long userId);

    ItemDto saveItem(long userId, ItemDto itemDto) throws ValidationException, AlreadyExistException, NotFoundException;

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId) throws NotFoundException;

    void deleteItem(long itemId);
}
