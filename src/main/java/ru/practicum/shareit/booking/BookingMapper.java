package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getBookingId(),
                booking.getFrom(), booking.getTo(),
                booking.getItem().getId(),
                booking.getApplicant().getId(),
                booking.getStatus());
    }
}
