package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldNotFindAnyUser_RepositoryEmpty() {
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    public void shouldStoreAUser() {
        User user = userRepository.save(User.builder().email("user1@email.com").name("user1").build());

        assertThat(user).hasFieldOrPropertyWithValue("id", user.getId());
        assertThat(user).hasFieldOrPropertyWithValue("email", user.getEmail());
        assertThat(user).hasFieldOrPropertyWithValue("name", user.getName());

    }

    @Test
    public void shouldFindAllUsersInRepository() {

        User user1 = userRepository.save(User.builder().email("user1@email.com").name("user1").build());
        User user2 = userRepository.save(User.builder().email("user2@email.com").name("user2").build());
        User user3 = userRepository.save(User.builder().email("user3@email.com").name("user3").build());


        List<User> userList = userRepository.findAll();

        assertThat(userList).hasSize(3).contains(user1, user2, user3);
    }

}