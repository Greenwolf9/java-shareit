package ru.practicum.shareit.item.dto;

import lombok.Value;

import java.util.List;

@Value
public class ItemDetails {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDetails lastBooking;
    BookingDetails nextBooking;
    List<CommentShort> comments;
}
