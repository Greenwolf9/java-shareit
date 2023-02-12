package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRepositoryIT {
    @Autowired
    ItemRepository itemRepository;

    @Test
    void findByItemIdAndUserId() {
    }

    @Test
    void findAllByRequestId() {
    }

    @Test
    void findItemByKeyWords() {
    }

}