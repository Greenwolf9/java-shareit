package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item findById(long itemId);

    Collection<Item> getAllItems();

    Collection<Item> findAllItemsByUserId(long userId);

    Item saveItem(Item item) throws ValidationException, AlreadyExistException;

    Item updateItem(Item item) throws NotFoundException;

    void deleteItem(long itemId);
}
