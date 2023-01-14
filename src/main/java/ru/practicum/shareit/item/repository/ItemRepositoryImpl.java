package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemsList = new HashMap<>();
    private int itemId = 0;

    @Override
    public Item findById(long itemId) {
        return itemsList.get(itemId);
    }

    @Override
    public Collection<Item> getAllItems() {
        return new ArrayList<>(itemsList.values());
    }

    @Override
    public Collection<Item> findAllItemsByUserId(long userId) {
        return itemsList.values().stream()
                .filter(x -> x.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(Item item) throws ValidationException, AlreadyExistException {
        validateItem(item);
        if (itemsList.values().stream().anyMatch(x -> x.getId() == item.getId())) {
            throw new AlreadyExistException("Item with id " + item.getId() + " already exists.");
        }
        item.setId(generateId());
        itemsList.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) throws NotFoundException {
        if (!itemsList.containsKey(item.getId())) {
            throw new NotFoundException("Item with id " + item.getId() + "doesn't exist.");
        }
        itemsList.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(long itemId) {
        itemsList.remove(itemId);
    }

    private long generateId() {
        return ++itemId;
    }

    private void validateItem(Item item) throws ValidationException {
        if (item.getName().isEmpty() && item.getName().equals("")) {
            throw new ValidationException("Name is invalid. Please check.");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().equals("")) {
            throw new ValidationException("Please add description.");
        }
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ValidationException("Please check availability.");
        }
    }
}
