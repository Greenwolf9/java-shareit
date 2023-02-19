package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody
                                                @Valid ItemRequestDto requestDto, BindingResult bindingResult)
            throws ValidationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Please validate description. Input is incorrect or empty.");
        }
        log.info("Create request{}, userId{} ", requestDto, userId);
        return requestClient.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by userId{} ", userId);
        return requestClient.getAllItemRequestsByUserId(userId);
    }

    @GetMapping(("/{requestId}"))
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        log.info("GET / requests/{requestId} : " + requestId);
        return requestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsSorted(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) throws ValidationException, NotFoundException {
        log.info("GET /requests/all?from={from}&size={size} ");
        return requestClient.getAllRequestsSorted(userId, from, size);
    }
}
