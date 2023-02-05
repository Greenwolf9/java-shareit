package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConversionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingInfoDto getBookingById(long bookingId, long userId) throws NotFoundException {
        final Booking booking = bookingRepository.findBookingByBookerIdOrOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " doesn't exist."));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingInfoDto> getAllBookingsByState(String state, Long userId) throws ConversionException, NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Booker is wrong");
        }
        List<Booking> result = new ArrayList<>();
        switch (convertToEnum(state)) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                result = bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(),
                                LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case UNSUPPORTED_STATUS:
                throw new ConversionException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toBookingDtoList(result);
    }

    @Override
    public List<BookingInfoDto> findAllByOwnerIdAndItem(Long ownerId, String state) throws ConversionException, NotFoundException {
        if (bookingRepository.findAllByOwnerIdAndItem(ownerId).isEmpty()) {
            throw new NotFoundException("Owner not found");
        }
        List<Booking> result = new ArrayList<>();
        switch (convertToEnum(state)) {
            case ALL:
                result = bookingRepository.findAllByOwnerIdAndItem(ownerId);
                break;
            case PAST:
                result = bookingRepository.findAllByOwnerIdAndItem(ownerId)
                        .stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                result = bookingRepository.findAllByOwnerIdAndItem(ownerId)
                        .stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                result = bookingRepository.findAllByOwnerIdAndItem(ownerId)
                        .stream()
                        .filter(x -> x.getStart().isBefore(LocalDateTime.now()) && x.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                result = bookingRepository.findAllByOwnerIdAndItemWithStatus(ownerId, Status.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByOwnerIdAndItemWithStatus(ownerId, Status.REJECTED);
                break;
            case UNSUPPORTED_STATUS:
                throw new ConversionException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toBookingDtoList(result);
    }

    private State convertToEnum(String state) {
        return State.valueOf(state);
    }

    @Override
    public List<Booking> getAllBookingsByUserId(long userId) {
        return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
    }

    @Override
    public BookingInfoDto saveBooking(long userId, BookingDto bookingDto) throws NotFoundException, ValidationException {
        final Booking booking = BookingMapper.toBooking(bookingDto);
        validateTimeOfBooking(booking);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist"));
        final Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + bookingDto.getItemId() + " doesn't exist"));
        if (item.getAvailable()) {
            booking.setStatus(Status.WAITING);
            booking.setBooker(user);
            booking.setItem(item);
        } else {
            throw new ValidationException("Item with id " + bookingDto.getItemId() + " is unavailable");
        }
        if (userId == booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Owner can't book own item");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingInfoDto updateBooking(long userId, long bookingId, boolean isApproved) throws NotFoundException, ValidationException {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " doesn't exist"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Unauthorised attempt to change status");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Booking is already approved");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        }
        if (!isApproved) {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBooking(long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    private void validateTimeOfBooking(Booking booking) throws ValidationException {
        LocalDateTime startTime = LocalDateTime.now();
        if (booking.getEnd().isBefore(startTime)) {
            throw new ValidationException("Please check end time");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isBefore(startTime)) {
            throw new ValidationException("Please check start time");
        }
    }
}
