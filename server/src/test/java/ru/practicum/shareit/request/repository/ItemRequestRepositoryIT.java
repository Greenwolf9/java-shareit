package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequestRepository;

@DataJpaTest
class ItemRequestRepositoryIT {
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
    }

    @Test
    void findAllRequestsByRequestorIdIsNot() {
    }
}