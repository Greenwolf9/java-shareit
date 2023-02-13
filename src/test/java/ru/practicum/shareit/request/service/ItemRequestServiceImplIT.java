package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.dto.ItemRequestWithListOfItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void getAllRequestsSorted_whenParametersValid_thenReturned() throws ValidationException {
        User testUser = new User();
        testUser.setName("test man");
        testUser.setEmail("test@test.com");
        User testRequestor = new User();
        testRequestor.setEmail("requestor@test.com");
        testRequestor.setName("Requestor");
        Item testItem = new Item(1L,
                "name",
                "description",
                true,
                null,
                null);
        userRepository.save(testUser);
        userRepository.save(testRequestor);
        testItem.setOwner(testUser);
        itemRepository.save(testItem);
        ItemRequest testRequest = new ItemRequest(1L, "Search for best thing ever", testRequestor, LocalDateTime.now());
        itemRequestRepository.save(testRequest);
        Pageable pageable = PageRequest.of(0, 20);
        Page<ItemRequestWithListOfItems> testPage = itemRequestRepository.findAllRequestsByRequestorIdIsNot(testUser.getId(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("created").descending()));
        List<ItemRequestDetails> testList = testPage.stream()
                .map(dto -> new ItemRequestDetails(dto.getId(),
                        dto.getDescription(),
                        dto.getCreated(),
                        itemRequestService.validateList(dto.getItems())))
                .collect(Collectors.toList());
        List<ItemRequestDetails> actualList = itemRequestService.getAllRequestsSorted(testUser.getId(), 0, 20);
        assertEquals(testList, actualList);
        assertEquals(testList.size(), actualList.size());
        assertThat(actualList, hasItems(testList.get(0)));
    }

    @Test
    void getAllRequestsSorted_whenParametersNotValid_thenThrownException() {
        User testUser = new User();
        testUser.setName("test man");
        testUser.setEmail("test@test.com");
        User testRequestor = new User();
        testRequestor.setEmail("requestor@test.com");
        testRequestor.setName("Requestor");
        Item testItem = new Item(1L,
                "name",
                "description",
                true,
                null,
                null);
        userRepository.save(testUser);
        userRepository.save(testRequestor);
        testItem.setOwner(testUser);
        itemRepository.save(testItem);
        ItemRequest testRequest = new ItemRequest(1L, "Search for best thing ever", testRequestor, LocalDateTime.now());
        itemRequestRepository.save(testRequest);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllRequestsSorted(testUser.getId(), -1, 20));
        assertEquals(validationException.getMessage(), "Input invalid. Check parameters.");
    }
}