package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(),
                user.getEmail(), user.getName());
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(),
                userDto.getEmail(), userDto.getName());
    }

    public static List<UserDto> toUserDtoList(Collection<User> users) {
        final List<UserDto> list = new ArrayList<>();
        for (User user : users) {
            list.add(toUserDto(user));
        }
        return list;
    }
}
