package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemDetailsForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.dto.ItemRequestWithListOfItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private User expectedUser;
    private ItemRequest expectedRequest;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        expectedUser = new User(1L, "test@test.ru", "Test User");
        expectedRequest = new ItemRequest(
                1L,
                "Search for Best Item ever",
                expectedUser,
                LocalDateTime.parse("2023-02-11T13:35:20", formatter));
    }

    @Test
    void saveItemRequest_whenRequestValid_thenSaved() throws Exception {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(expectedRequest);

        ItemRequestDetails actualRequest = itemRequestService.saveItemRequest(userId, RequestMapper.toDto(expectedRequest));

        assertEquals(RequestMapper.toItemRequestDto(expectedRequest), actualRequest);
    }

    @Test
    void saveItemRequest_whenRequestNotValid_thenThrownException() {
        Long userId = 1L;
        ItemRequest testRequest = new ItemRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        ValidationException validationException = assertThrows(ValidationException.class, () -> itemRequestService.saveItemRequest(userId, RequestMapper.toDto(testRequest)));

        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
        assertEquals(validationException.getMessage(), "Please validate description. Input is incorrect or empty.");
    }

    @Test
    void getItemRequestById_whenFound_thenReturned() throws Exception {
        Long requestId = 1L;
        Long userId = 1L;
        when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.getById(requestId)).thenReturn(expectedRequest);

        ItemRequestDetails actualRequest = itemRequestService.getItemRequestById(requestId, userId);

        assertEquals(RequestMapper.toItemRequestDto(expectedRequest), actualRequest);
    }

    @Test
    void getItemRequestById_whenNotFound_thenThrownException() {
        Long requestId = 1L;
        Long userId = 1L;

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, userId));

        assertEquals(notFoundException.getMessage(), "Request with id " + requestId + " doesn't exist.");
    }

    @Test
    void getAllItemRequestsByUserId_whenAuthorized_thenReturned() throws Exception {
        Long userId = expectedUser.getId();
        ItemDetailsForRequest item = new ItemDetailsForRequest(1L, "TestItem", "Test Description", true, 1L);

        when(userRepository.existsById(userId)).thenReturn(true);
        ItemRequestWithListOfItems testItem = new ItemRequestWithListOfItems() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getDescription() {
                return "Test Description";
            }

            @Override
            public LocalDateTime getCreated() {
                return LocalDateTime.now();
            }

            @Override
            public List<ItemDetailsForRequest> getItems() {
                return List.of(item);
            }
        };
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(testItem));
        List<ItemRequestDetails> actualList = itemRequestService.getAllItemRequestsByUserId(userId);
        assertFalse(actualList.isEmpty());
        assertEquals(1, actualList.size());

    }

    @Test
    void getAllItemRequestsByUserId_whenNotAuthorized_thenThrownException() {
        Long userId = expectedUser.getId();
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequestsByUserId(userId));
        assertEquals(notFoundException.getMessage(), "Requestor is wrong");
    }


    @Test
    void deleteItemRequest() {
        doNothing().when(itemRequestRepository).deleteById(1L);

        itemRequestService.deleteItemRequest(expectedRequest.getId());

        verify(itemRequestRepository, times(1)).deleteById(1L);
    }
}