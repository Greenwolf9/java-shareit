package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Create item {}, userId{}", itemDto, userId);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("PATCH / items/{itemId}: " + itemId + " posted by user" + userId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody CommentDto commentDto) {
        return itemClient.addComments(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDto(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        log.info("Get item with id{}, userId{} ", itemId, userId);
        return itemClient.getNextAndLastBookingsOfItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId{}, from{}, size{}", userId, from, size);
        return itemClient.findListOfItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchOfItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @NotNull @RequestParam String text,
                                                @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET / items: search items with text " + text);
        return itemClient.getSearchedItems(text, userId, from, size);
    }
}
