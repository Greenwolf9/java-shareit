package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookerShort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.ItemForBookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingDetails;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingInfoDto toBookingDto(Booking booking) {
        final ItemForBookingInfoDto item = new ItemForBookingInfoDto(booking.getItem().getId(), booking.getItem().getName());
        final BookerShort booker = new BookerShort(booking.getBooker().getId(), booking.getBooker().getName());
        return new BookingInfoDto(booking.getId(),
                booking.getStart(), booking.getEnd(),
                item,
                booker,
                booking.getStatus());
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
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

    public static BookingDetails mapToLastBooking(Long lastBookingId, Long lastBookingBookerId) {
        if (lastBookingId == null || lastBookingBookerId == null) {
            return null;
        }
        return Optional.of(new BookingDetails(lastBookingId, lastBookingBookerId)).orElse(null);
    }

    public static BookingDetails mapToNextBooking(Long nextBookingId, Long nextBookingBookerId) {
        if (nextBookingId == null || nextBookingBookerId == null) {
            return null;
        }
        return Optional.of(new BookingDetails(nextBookingId, nextBookingBookerId)).orElse(null);
    }

    public static List<BookingInfoDto> toBookingDtoList(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
