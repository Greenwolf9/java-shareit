package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDetails;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIT {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemServiceImpl itemService;

    @Test
    void findListOfItemsByUserId() {
        User testUser = new User(1L, "test@test.com", "test man");
        User testRequestor = new User(2L, "requestor@test.com", "Requestor");

        Item testItem = new Item(1L,
                "name",
                "description",
                true,
                testUser,
                null);
        userRepository.save(testUser);
        userRepository.save(testRequestor);
        itemRepository.save(testItem);


        Pageable pageable = PageRequest.of(0, 20);
        Page<ItemView> testPageOfItems = itemRepository
                .findByItemIdAndUserId(testUser.getId(), LocalDateTime.now(), pageable);
        List<ItemDetails> testList = testPageOfItems.stream().map(dto -> new ItemDetails(dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                BookingMapper.mapToLastBooking(dto.getLastBookingId(), dto.getLastBookingBookerId()),
                BookingMapper.mapToNextBooking(dto.getNextBookingId(), dto.getNextBookingBookerId()),
                CommentMapper.toCommentShortList(commentRepository.findAllByItemId(dto.getId())))).collect(Collectors.toList());
        List<ItemDetails> actualList = itemService
                .findListOfItemsByUserId(
                        testUser.getId(),
                        LocalDateTime.now(),
                        pageable.getPageNumber(),
                        pageable.getPageSize());
        assertEquals(testList, actualList);
        assertEquals(testList.size(), actualList.size());
    }
}