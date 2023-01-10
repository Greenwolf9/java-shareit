package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    Collection<User> findAll();

    User findById(long userId);

    User saveUser(User user) throws AlreadyExistException, ValidationException;

    User updateUser(User user) throws NotFoundException;

    void deleteUser(long userId);
}
