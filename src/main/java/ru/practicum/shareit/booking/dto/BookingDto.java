package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Value
public class BookingDto {

    long id;
    LocalDateTime start;
    LocalDateTime end;
    long itemId;
    long bookerId;
    Status status;
}

