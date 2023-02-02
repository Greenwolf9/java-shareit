package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Value
public class BookingInfoDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemShort item;
    BookerShort booker;
    Status status;
}
