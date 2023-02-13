package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User expectedUser;

    @BeforeEach
    void setUp() {
        expectedUser = new User(1L, "test@test.ru", "Test User");
    }

    @Test
    void getAllUsers() {

        when(userRepository.findAll()).thenReturn(List.of(expectedUser));

        List<UserDto> listOfUsers = userService.getAllUsers();

        assertEquals(UserMapper.toUserDtoList(List.of(expectedUser)), listOfUsers);
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() throws NotFoundException {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.getUserById(userId);

        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenExceptionThrown() {
        long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals(notFoundException.getMessage(), "User with id " + userId + " doesn't exist");
    }

    @Test
    void saveUser_whenUserValid_thenSaved() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserDto actualUser = userService.saveUser(UserMapper.toUserDto(expectedUser));

        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void saveUser_whenUserNotValid_thenExceptionThrown() {

        User invalidUser = new User(2L, "test.ru", "Invalid User");

        assertThrows(ValidationException.class,
                () -> userService.saveUser(UserMapper.toUserDto(invalidUser)));

        verify(userRepository, never()).save(invalidUser);
    }

    @Test
    void updateUser_whenEmailIsUnique_thenReturned() throws NotFoundException, AlreadyExistException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        User toBeUpdatedUser = expectedUser;
        toBeUpdatedUser.setEmail("update@test.ru");
        toBeUpdatedUser.setName("Test UpdatedUser");
        when(userRepository.save(toBeUpdatedUser)).thenReturn(toBeUpdatedUser);

        UserDto actualUser = userService.updateUser(1L, UserMapper.toUserDto(toBeUpdatedUser));

        assertEquals(UserMapper.toUserDto(toBeUpdatedUser), actualUser);
        verify(userRepository).save(toBeUpdatedUser);
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(expectedUser.getId());
        verify(userRepository, times(1)).deleteById(1L);
    }
}