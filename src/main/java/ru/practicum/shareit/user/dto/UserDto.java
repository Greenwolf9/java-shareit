package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class UserDto {
    Long id;
    @NotNull
    String email;
    String name;
}
