package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid BookingDto bookingDto, BindingResult bindingResult)
            throws ValidationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Check start or end time");
        }
        log.info("POST /bookings: id " + bookingDto.getId());
        return bookingClient.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable long bookingId,
                                                @RequestParam(value = "approved", defaultValue = "null") Boolean isApproved) {
        log.info("POST /bookings/{bookingId}?approved = : id " + bookingId);
        return bookingClient.updateBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        log.info("GET /bookings/{bookingId}: id " + bookingId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByStateAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET /bookings?state={state} " + state);
        return bookingClient.getAllBookingsByState(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByStateAndItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                    @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                                    @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                    @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET /bookings/owner?state={state} " + state);
        return bookingClient.findAllByOwnerIdAndItem(userId, state, from, size);
    }
}
