package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long itemId) throws NotFoundException;

    List<ItemDto> getAllItems();

    ItemDto saveItem(Long userId, ItemDto itemDto) throws ValidationException, AlreadyExistException, NotFoundException;

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) throws NotFoundException;

    void deleteItem(Long itemId);

    ItemDetails getNextAndLastBookingsOfItem(long itemId, long userId) throws NotFoundException;

    CommentShort addComments(Long userId, Long itemId, CommentDto commentDto) throws ValidationException, NotFoundException;

    List<ItemDetails> findListOfItemsByUserId(Long userId, LocalDateTime dateTime);
}
