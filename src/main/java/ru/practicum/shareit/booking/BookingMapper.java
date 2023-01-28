package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookerShort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.ItemShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingInfoDto toBookingDto(Booking booking) {
        final ItemShort item = new ItemShort(booking.getItem().getId(), booking.getItem().getName());
        final BookerShort booker = new BookerShort(booking.getBooker().getId(), booking.getBooker().getName());
        return new BookingInfoDto(booking.getId(),
                booking.getStart(), booking.getEnd(),
                item,
                booker,
                booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        final Item item = new Item();
        item.setId(bookingDto.getItemId());
        final User booker = new User();
        booker.setId(bookingDto.getBookerId());
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus());
    }

    public static List<BookingInfoDto> toBookingDtoList(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
