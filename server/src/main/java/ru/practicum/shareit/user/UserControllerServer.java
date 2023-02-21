package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserControllerServer {

    private final UserService service;

    @Autowired
    public UserControllerServer(UserService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) throws NotFoundException {
        log.info("GET /users/{userId}: " + userId);
        return service.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /users: " + service.getAllUsers().size());
        return service.getAllUsers();
    }

    @PostMapping
    public UserDto addNewUser(@RequestBody UserDto userDto) throws ValidationException {
        log.info("POST /users: " + userDto.getEmail());
        return service.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) throws AlreadyExistException, NotFoundException {
        log.info("PATCH /users/{userId}: " + userId);
        return service.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE /users/{userId}: " + userId);
        service.deleteUser(userId);
    }
}