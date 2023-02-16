package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.ConversionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {
    BookingInfoDto getBookingById(long bookingId, long userId) throws NotFoundException;

    List<BookingInfoDto> getAllBookingsByState(String state, Long userId, Integer from, Integer size) throws ConversionException, NotFoundException, ValidationException;

    BookingInfoDto saveBooking(long userId, BookingDto bookingDto) throws ValidationException, NotFoundException;

    BookingInfoDto updateBooking(long userId, long bookingId, boolean isApproved) throws NotFoundException, ValidationException;

    List<BookingInfoDto> findAllByOwnerIdAndItem(Long ownerId, String state, Integer from, Integer size) throws ConversionException, NotFoundException, ValidationException;
}
