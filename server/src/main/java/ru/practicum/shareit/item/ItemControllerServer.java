package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/items")
public class ItemControllerServer {
    private final ItemService itemService;

    @Autowired
    public ItemControllerServer(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) throws ValidationException,
            AlreadyExistException,
            NotFoundException {
        log.info("POST / items: posted by user" + userId);
        return itemService.saveItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentShort addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId,
                                      @RequestBody CommentDto commentDto) throws ValidationException, NotFoundException {
        return itemService.addComments(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDetails getItemDto(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId) throws NotFoundException {
        log.info("GET / items/{id}: " + itemId);
        return itemService.getNextAndLastBookingsOfItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDetails> getAllItemOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.info("GET / items: posted by user" + userId);
        return itemService.findListOfItemsByUserId(userId, LocalDateTime.now(), from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) throws NotFoundException {
        log.info("PATCH / items/{itemId}: " + itemId + " posted by user" + userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchOfItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam String text,
                                       @RequestParam(value = "from", defaultValue = "0") Integer from,
                                       @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.info("GET / items: search items with text " + text);
        return itemService.getSearchedItems(text, from, size);
    }
}