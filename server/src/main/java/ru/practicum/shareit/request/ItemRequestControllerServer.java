package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestControllerServer {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestControllerServer(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDetails addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto requestDto) throws ValidationException, NotFoundException {
        log.info("POST /requests, done by user: " + userId);
        return itemRequestService.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDetails> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        log.info("GET / requests : by userId " + userId);
        return itemRequestService.getAllItemRequestsByUserId(userId);
    }

    @GetMapping(("/{requestId}"))
    public ItemRequestDetails getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) throws NotFoundException {
        log.info("GET / requests/{requestId} : " + requestId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDetails> getAllRequestsSorted(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(value = "size", defaultValue = "20") Integer size) throws ValidationException, NotFoundException {
        log.info("GET /requests/all?from={from}&size={size} ");
        return itemRequestService.getAllRequestsSorted(userId, from, size);
    }
}