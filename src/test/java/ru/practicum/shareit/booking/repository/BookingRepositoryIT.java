package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryIT {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void addBookingToRepository() {
        owner = userRepository.save(User.builder().email("owner@test.com").name("owner").build());
        booker = userRepository.save(User.builder().email("booker@test.com").name("booker").build());
        item = itemRepository.save(Item.builder().name("item")
                .description("item for booking")
                .available(true).owner(owner).build());
        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(50))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build());
    }

    @Test
    void findAllByItemId() {
        List<Booking> bookingList = bookingRepository.findAllByItemId(item.getId());
        assertThat(bookingList).isNotEmpty().hasSize(1);
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);
        assertThat(pageOfBooking).isNotEmpty().hasSize(1);
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), booking.getStatus(), pageable);
        assertThat(pageOfBooking).isNotEmpty().hasSize(1);
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), pageable);
        assertThat(pageOfBooking).isNotEmpty().hasSize(1);
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now(), pageable);
        assertThat(pageOfBooking).isEmpty();
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(booker.getId(),
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
        assertThat(pageOfBooking).isEmpty();
    }

    @Test
    void findAllByOwnerId() {
        List<Booking> bookingList = bookingRepository.findAllByOwnerId(owner.getId());
        assertThat(bookingList).isNotEmpty().hasSize(1);

    }

    @Test
    void findAllByOwnerIdAndItem() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByOwnerIdAndItem(owner.getId(), pageable);
        assertThat(pageOfBooking).isNotEmpty().hasSize(1);
    }

    @Test
    void findAllByOwnerIdAndItemWithStatus() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> pageOfBooking = bookingRepository
                .findAllByOwnerIdAndItemWithStatus(owner.getId(), booking.getStatus(), pageable);
        assertThat(pageOfBooking).isNotEmpty().hasSize(1);
    }

    @Test
    void findBookingByBookerIdOrOwnerId() {
        Optional<Booking> booking1 = bookingRepository.findBookingByBookerIdOrOwnerId(booking.getId(), booker.getId());
        assertThat(booking1).isNotEmpty();
        assertThat(booking1.get()).hasFieldOrPropertyWithValue("id", booking1.get().getId());
        assertThat(booking1.get()).hasFieldOrPropertyWithValue("start", booking1.get().getStart());
        assertThat(booking1.get()).hasFieldOrPropertyWithValue("end", booking1.get().getEnd());
        assertThat(booking1.get()).hasFieldOrPropertyWithValue("status", booking1.get().getStatus());
    }

    @AfterEach
    void deleteAllRepositories() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}