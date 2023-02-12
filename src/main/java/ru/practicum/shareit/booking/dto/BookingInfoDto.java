package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Value
public class BookingInfoDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemForBookingInfoDto item;
    BookerShort booker;
    Status status;
}
