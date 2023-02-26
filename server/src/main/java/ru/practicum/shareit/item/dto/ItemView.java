package ru.practicum.shareit.item.dto;

public interface ItemView {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    Long getLastBookingId();

    Long getLastBookingBookerId();

    Long getNextBookingId();

    Long getNextBookingBookerId();
}
