package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInfoDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShort item;
    private BookerShort booker;
    private Status status;
}
