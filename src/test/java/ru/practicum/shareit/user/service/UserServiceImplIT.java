package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserServiceImpl userService;

    @Test
    void getAllUsers() {
        User testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setName("test man");
        userRepository.save(testUser);
        List<UserDto> listOfTestUser = userService.getAllUsers();
        assertEquals(UserMapper.toUserDtoList(List.of(testUser)), listOfTestUser);
    }
}