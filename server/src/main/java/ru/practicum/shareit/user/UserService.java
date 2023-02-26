package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId) throws NotFoundException;

    UserDto saveUser(UserDto userDto) throws ValidationException;

    UserDto updateUser(Long userId, UserDto userDto) throws AlreadyExistException, NotFoundException;

    void deleteUser(Long userId);
}
