package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto) throws ValidationException,
            AlreadyExistException,
            NotFoundException {
        log.info("POST / items: posted by user" + userId);
        return itemService.saveItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDto(@PathVariable long itemId) {
        log.info("GET / items/{id}: " + itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET / items: posted by user" + userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) throws NotFoundException {
        log.info("PATCH / items/{itemId}: " + itemId + " posted by user" + userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchOfItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam String text) {
        List<ItemDto> itemsDto = itemService.getAllItems();

        log.info("GET / items: search items with text " + text);
        return itemsDto.stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase())
                        && x.getAvailable()
                        && !text.isEmpty())
                .collect(Collectors.toList());
    }
}
