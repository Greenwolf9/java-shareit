package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private int userId = 0;
    protected final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public User saveUser(User user) throws ValidationException, AlreadyExistException {
        validateUser(user);
        if (users.values().stream().map(User::getEmail).anyMatch(x -> x.equals(user.getEmail()))) {
            throw new AlreadyExistException("User with email " + user.getEmail() + " already exists.");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User doesn't exist.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    // -----------------------------------------------------------------------------------------
    private long generateId() {
        return ++userId;
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email is invalid.");
        }
    }
}
