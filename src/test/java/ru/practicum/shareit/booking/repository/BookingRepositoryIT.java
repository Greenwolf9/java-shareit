package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookingRepositoryIT {
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void findAllByItemId() {
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
    }

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void findAllByOwnerIdAndItem() {
    }

    @Test
    void findAllByOwnerIdAndItemWithStatus() {
    }

    @Test
    void findBookingByBookerIdOrOwnerId() {
    }
}