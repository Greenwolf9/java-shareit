package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDetails {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDetails lastBooking;
    private BookingDetails nextBooking;
    private List<CommentShort> comments;
}
