package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIT {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Test
    void getAllRequestsSorted() throws ValidationException {
        User testUser = new User(1L, "test@test.com", "test man");
        User testRequestor = new User(2L, "requestor@test.com", "Requestor");
        ItemRequest testRequest = new ItemRequest(1L, "Search for best thing ever", testRequestor, LocalDateTime.now());
        Item testItem = new Item(1L,
                "name",
                "description",
                true,
                testUser,
                null);
        userRepository.save(testUser);
        userRepository.save(testRequestor);
        itemRequestRepository.save(testRequest);
        itemRepository.save(testItem);
        Pageable pageable = PageRequest.of(0, 20);

        List<ItemRequestDetails> testList = itemRequestRepository.findAllRequestsByRequestorIdIsNot(testRequestor.getId(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("created").descending())).stream()
                .map(dto -> new ItemRequestDetails(dto.getId(),
                        dto.getDescription(),
                        dto.getCreated(),
                        dto.getItems()))
                .collect(Collectors.toList());
        List<ItemRequestDetails> actualList = itemRequestService.getAllRequestsSorted(testRequestor.getId(), 0, 20);
        assertEquals(testList, actualList);
        assertEquals(testList.size(), actualList.size());
    }
}