package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(long bookingId);

    List<Booking> getAllBookingsByUserId(long userId);

    Booking saveBooking(Booking booking);

    Booking updateBooking(long bookingId);

    Booking deleteBooking(long bookingId);
}
