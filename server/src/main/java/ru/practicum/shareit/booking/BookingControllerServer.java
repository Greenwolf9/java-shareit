package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.ConversionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingControllerServer {
    private final BookingService bookingService;

    @Autowired
    public BookingControllerServer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody BookingDto bookingDto) throws ValidationException, NotFoundException {
        log.info("POST /bookings: id " + bookingDto.getId());
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable long bookingId,
                                        @RequestParam(value = "approved") boolean isApproved) throws NotFoundException, ValidationException {
        log.info("POST /bookings/{bookingId}?approved = : id " + bookingId);
        return bookingService.updateBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) throws NotFoundException {
        log.info("GET /bookings/{bookingId}: id " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingInfoDto> getBookingsByStateAndUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                            @RequestParam(value = "size", defaultValue = "20") Integer size)
            throws ConversionException, NotFoundException, ValidationException {
        log.info("GET /bookings?state={state} " + state);
        return bookingService.getAllBookingsByState(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getBookingsByStateAndItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                  @RequestParam(value = "size", defaultValue = "20") Integer size)
            throws ConversionException, NotFoundException, ValidationException {
        log.info("GET /bookings/owner?state={state} " + state);
        return bookingService.findAllByOwnerIdAndItem(userId, state, from, size);
    }
}