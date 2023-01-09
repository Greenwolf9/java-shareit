package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        final Item item = itemRepository.findItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        final Collection<Item> items = itemRepository.getAllItems();
        return ItemMapper.toItemDtoList(items);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        final Collection<Item> items = itemRepository.findAllItems(userId);
        return ItemMapper.toItemDtoList(items);
    }

    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) throws ValidationException, AlreadyExistException, NotFoundException {
        final User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User of this item is not found. Please check.");
        }
        final Item item = itemRepository.saveItem(ItemMapper.toItem(itemDto));
        item.setOwner(user);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId) throws NotFoundException {
        final Item itemToBeUpdated = itemRepository.findItem(itemId);
        if (itemToBeUpdated.getOwner().getId() != userId) {
            throw new NotFoundException("Wrong userId. Please check");
        }
        if (itemDto.getName() != null && !itemDto.getName().equals(itemToBeUpdated.getName())) {
            itemToBeUpdated.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(itemToBeUpdated.getDescription())) {
            itemToBeUpdated.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(itemToBeUpdated.getAvailable())) {
            itemToBeUpdated.setAvailable(itemDto.getAvailable());
        }
        final Item item = itemRepository.updateItem(itemToBeUpdated);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }
}
