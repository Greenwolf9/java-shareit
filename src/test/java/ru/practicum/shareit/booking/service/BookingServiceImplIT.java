package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConversionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    BookingServiceImpl bookingService;

    @Test
    void getAllBookingsByState() throws Exception {
        User testUser = new User(1L, "test@test.com", "test man");
        User testBooker = new User(2L, "requestor@test.com", "Requestor");

        Item testItem = new Item(1L,
                "name",
                "description",
                true,
                testUser,
                null);
        Booking testBooking = new Booking(1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                testItem, testBooker,
                Status.APPROVED);
        userRepository.save(testUser);
        userRepository.save(testBooker);
        itemRepository.save(testItem);
        bookingRepository.save(testBooking);
        Pageable pageable = PageRequest.of(0, 20);

        List<Booking> testListCaseAll = bookingRepository.findAllByBookerIdOrderByStartDesc(testBooker.getId(), pageable).getContent();
        List<BookingInfoDto> actualListCaseAll = bookingService.getAllBookingsByState("ALL", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(BookingMapper.toBookingDtoList(testListCaseAll), actualListCaseAll);

        List<Booking> testListCaseFuture = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(testBooker.getId(), LocalDateTime.now(), pageable).getContent();
        List<BookingInfoDto> actualListCaseFuture = bookingService.getAllBookingsByState("FUTURE", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(BookingMapper.toBookingDtoList(testListCaseFuture), actualListCaseFuture);

        List<Booking> testListCasePast = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(testBooker.getId(), LocalDateTime.now(), pageable).getContent();
        List<BookingInfoDto> actualListCasePast = bookingService.getAllBookingsByState("PAST", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(BookingMapper.toBookingDtoList(testListCasePast), actualListCasePast);

        List<Booking> testListCaseCurrent = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(testBooker.getId(), LocalDateTime.now(), LocalDateTime.now(), pageable).getContent();
        List<BookingInfoDto> actualListCaseCurrent = bookingService.getAllBookingsByState("CURRENT", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(BookingMapper.toBookingDtoList(testListCaseCurrent), actualListCaseCurrent);

        List<Booking> testListCaseWaiting = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(testBooker.getId(), Status.WAITING, pageable).getContent();
        List<BookingInfoDto> actualListCaseWaiting = bookingService.getAllBookingsByState("WAITING", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(BookingMapper.toBookingDtoList(testListCaseWaiting), actualListCaseWaiting);

        List<Booking> testListCaseRejected = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(testBooker.getId(), Status.REJECTED, pageable).getContent();
        List<BookingInfoDto> actualListCaseRejected = bookingService.getAllBookingsByState("REJECTED", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(BookingMapper.toBookingDtoList(testListCaseRejected), actualListCaseRejected);

        ConversionException conversionException = assertThrows(ConversionException.class,
                () -> bookingService.getAllBookingsByState("UNSUPPORTED_STATUS", testBooker.getId(), pageable.getPageNumber(), pageable.getPageSize()));
        assertEquals(conversionException.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }
}