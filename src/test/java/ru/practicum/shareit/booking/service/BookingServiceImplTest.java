package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConversionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User expectedUser;
    private User booker;
    private Item expectedItem;
    private Booking expectedBooking;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        expectedUser = new User(1L, "test@test.ru", "Test User");
        booker = new User(2L, "test2@test.com", "Test man");
        expectedItem = new Item(1L,
                "Test ItemName",
                "Test ItemDescription",
                true, expectedUser,
                new ItemRequest());

        expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.parse("2023-06-14T13:35:20", formatter));
        expectedBooking.setEnd(LocalDateTime.parse("2023-06-15T13:35:20", formatter));
        expectedBooking.setBooker(booker);
        expectedBooking.setItem(expectedItem);
    }

    @Test
    void getBookingById_whenFound_thenReturned() throws Exception {
        long bookingId = 1L;
        long bookerId = 2L;
        when(bookingRepository.findBookingByBookerIdOrOwnerId(bookerId, bookingId)).thenReturn(Optional.of(expectedBooking));
        BookingInfoDto bookingInfoDto = bookingService.getBookingById(bookerId, bookingId);
        assertEquals(BookingMapper.toBookingDto(expectedBooking), bookingInfoDto);
    }

    @Test
    void getBookingById_whenNotFound_thenThrownException() {
        long bookerId = 2L;
        long bookingId = 3L;
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(bookingId, bookerId));
        assertEquals(notFoundException.getMessage(), "Booking with id " + bookingId + " doesn't exist.");
    }

    @Test
    void getAllBookingsByState() throws Exception {
        String all = "ALL";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.getAllBookingsByState(all, booker.getId(), pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);
        assertEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);
    }

    @Test
    void findAllByOwnerIdAndItem_whenStateIsAll() throws Exception {
        String text = "ALL";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerIdAndItem(anyLong(), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByOwnerIdAndItem(expectedUser.getId(), pageable);
        assertEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);

    }

    @Test
    void findAllByOwnerIdAndItem_whenStateIsFuture() throws Exception {
        String text = "FUTURE";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerIdAndItem(anyLong(), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByOwnerIdAndItem(expectedUser.getId(), pageable);
        assertEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);
    }

    @Test
    void findAllByOwnerIdAndItem_whenStateIsPast() throws Exception {
        String text = "PAST";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerIdAndItem(anyLong(), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByOwnerIdAndItem(expectedUser.getId(), pageable);
        assertNotEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByOwnerIdAndItem_whenStateIsCurrent() throws Exception {
        String text = "CURRENT";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerIdAndItem(anyLong(), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByOwnerIdAndItem(expectedUser.getId(), pageable);
        assertNotEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByOwnerIdAndItem_whenStatusIsWaiting() throws Exception {
        String text = "WAITING";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerIdAndItemWithStatus(anyLong(), any(Status.class), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByOwnerIdAndItemWithStatus(expectedUser.getId(), Status.WAITING, pageable);
        assertEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);
    }

    @Test
    void findAllByOwnerIdAndItem_whenStatusIsRejected() throws Exception {
        String text = "REJECTED";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> page = new PageImpl<>(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        when(bookingRepository.findAllByOwnerIdAndItemWithStatus(anyLong(), any(Status.class), any(Pageable.class))).thenReturn(page);

        List<BookingInfoDto> bookings = bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize());

        verify(bookingRepository).findAllByOwnerIdAndItemWithStatus(expectedUser.getId(), Status.REJECTED, pageable);
        assertEquals(BookingMapper.toBookingDtoList(page.getContent()), bookings);
    }

    @Test
    void findAllByOwnerIdAndItem_whenStatusIsUnsupported_thenThrownException() {
        String text = "UNSUPPORTED_STATUS";
        Pageable pageable = PageRequest.of(0, 20);
        when(bookingRepository.findAllByOwnerId(expectedUser.getId())).thenReturn(List.of(expectedBooking));
        ConversionException conversionException = assertThrows(ConversionException.class,
                () -> bookingService.findAllByOwnerIdAndItem(expectedUser.getId(), text, pageable.getPageNumber(), pageable.getPageSize()));

        assertEquals(conversionException.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void saveBooking_whenBookingValid_thenSaved() throws Exception {
        Long userId = booker.getId();
        when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(expectedItem));
        BookingInfoDto actualBooking = bookingService.saveBooking(userId, BookingMapper.toDto(expectedBooking));
        assertEquals(BookingMapper.toBookingDto(expectedBooking), actualBooking);
    }

    @Test
    void saveBooking_whenBookingNotValid_thenThrownException() {
        Long userId = booker.getId();
        expectedBooking.setStart(LocalDateTime.now().minusHours(36));
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(userId, BookingMapper.toDto(expectedBooking)));
        assertEquals(validationException.getMessage(), "Please check start time");
    }

    @Test
    void updateBooking() throws Exception {
        long bookingId = 1L;
        Long userId = expectedUser.getId();
        boolean approved = false;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        Booking toBeUpdated = expectedBooking;
        toBeUpdated.setStatus(Status.REJECTED);
        when(bookingRepository.save(toBeUpdated)).thenReturn(toBeUpdated);

        BookingInfoDto actualBooking = bookingService.updateBooking(userId, bookingId, approved);

        assertEquals(BookingMapper.toBookingDto(toBeUpdated), actualBooking);
        verify(bookingRepository).save(toBeUpdated);
    }
}