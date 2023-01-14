package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(int userId);

    UserDto saveUser(UserDto userDto) throws ValidationException, AlreadyExistException;

    UserDto updateUser(int userId, UserDto userDto) throws AlreadyExistException, NotFoundException;

    void deleteUser(int userId);
}
