package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        final Collection<User> users = repository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    @Override
    public UserDto getUserById(int userId) {
        final User user = repository.findById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) throws ValidationException, AlreadyExistException {
        final User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.saveUser(user));
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) throws AlreadyExistException, NotFoundException {
        User userToBeUpdated = Optional.of(repository.findById(userId))
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist"));

        if (userDto.getEmail() != null) {
            checkIfEmailAlreadyExist(userDto.getEmail());
            userToBeUpdated.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userToBeUpdated.setName(userDto.getName());
        }
        return UserMapper.toUserDto(repository.updateUser(userToBeUpdated));
    }


    @Override
    public void deleteUser(int userId) {
        repository.deleteUser(userId);
    }

    //---------------------------------------------------------------------------------
    private void checkIfEmailAlreadyExist(String email) throws AlreadyExistException {
        if (repository.findAll()
                .stream()
                .map(User::getEmail)
                .anyMatch(x -> x.equals(email))) {
            throw new AlreadyExistException("User with email " + email + " already exists");
        }
    }
}
