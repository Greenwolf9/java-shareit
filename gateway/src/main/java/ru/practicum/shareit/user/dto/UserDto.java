package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
public class UserDto {
    Long id;
    @NotNull
    @NotEmpty(message = "Email shouldn't be empty")
    @Email(message = "Email is incorrect. Please check")
    String email;
    String name;
}
