package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;
    private User expectedUser;
    private User author;
    private Item expectedItem;
    private Booking expectedBooking;
    private Comment comment;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        expectedUser = new User(1L, "test@test.ru", "Test User");
        author = new User(2L, "test2@test.com", "Test man");
        expectedItem = new Item(1L,
                "Test ItemName",
                "Test ItemDescription",
                true, expectedUser,
                new ItemRequest());

        expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.parse("2023-02-08T13:35:20", formatter));
        expectedBooking.setEnd(LocalDateTime.parse("2023-02-09T13:35:20", formatter));
        expectedBooking.setBooker(author);
        expectedBooking.setItem(expectedItem);

        comment = new Comment();
        comment.setText("Best Item ever");
        comment.setAuthor(author);
        comment.setItem(expectedItem);
        comment.setCreated(LocalDateTime.parse(LocalDateTime.now().format(formatter)));
    }

    @Test
    void getItemById_whenFound_thenReturned() throws NotFoundException {
        long itemId = 1L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(expectedItem));
        ItemDto actualItem = itemService.getItemById(itemId);
        assertEquals(ItemMapper.toItemDto(expectedItem), actualItem);
    }

    @Test
    void getItemById_whenNotFound_thenThrownException() {
        long itemId = 0L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(itemId));
        assertEquals(notFoundException.getMessage(), "Such Item with id " + itemId + " doesn't exist.");
    }

    @Test
    void getSearchedItems_whenSearchParametersNotEmpty_thenReturned() {
        String text = "text";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Item> page = new PageImpl<>(List.of(expectedItem));
        when(itemRepository.findItemByKeyWords(anyString(), any(Pageable.class))).thenReturn(page);

        List<ItemDto> items = itemService.getSearchedItems(text, pageable.getPageNumber(), pageable.getPageSize());

        verify(itemRepository).findItemByKeyWords(text, pageable);
        assertEquals(ItemMapper.toItemDtoList(page.getContent()), items);
    }

    @Test
    void getSearchedItems_whenSearchParametersEmpty_thenReturnedEmptyList() {
        String text = "";
        Pageable pageable = PageRequest.of(0, 20);

        List<ItemDto> items = itemService.getSearchedItems(text, pageable.getPageNumber(), pageable.getPageSize());

        verify(itemRepository, never()).findItemByKeyWords(text, pageable);
        assertEquals(Collections.emptyList(), items);
    }

    @Test
    void saveItem_whenValidItem_thenSaved() throws ValidationException, NotFoundException {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRepository.save(any(Item.class))).thenReturn(expectedItem);

        ItemDto actualItem = itemService.saveItem(userId, ItemMapper.toItemDto(expectedItem));

        assertEquals(ItemMapper.toItemDto(expectedItem), actualItem);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void saveItem_whenNotValidItem_thenThrownException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        Item item = new Item();
        item.setName("");

        assertThrows(ValidationException.class,
                () -> itemService.saveItem(userId,
                        ItemMapper.toItemDto(item)));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_whenOwnerAuthorized_thenReturnedItem() throws NotFoundException {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        Item toBeUpdated = expectedItem;
        toBeUpdated.setAvailable(false);
        toBeUpdated.setDescription("Test UpdatedDescription");
        when(itemRepository.save(toBeUpdated)).thenReturn(toBeUpdated);

        ItemDto actualItem = itemService.updateItem(itemId, ItemMapper.toItemDto(toBeUpdated), userId);

        assertEquals(ItemMapper.toItemDto(toBeUpdated), actualItem);
        verify(itemRepository).save(toBeUpdated);
    }

    @Test
    void updateItem_whenOwnerNotAuthorized_thenThrownException() {
        Long userId = 2L;
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        Item toBeUpdated = expectedItem;
        toBeUpdated.setAvailable(false);
        toBeUpdated.setDescription("Test UpdatedDescription");

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemId, ItemMapper.toItemDto(toBeUpdated), userId));

        assertEquals(notFoundException.getMessage(), "Wrong userId. Please check");
        verify(itemRepository, never()).save(toBeUpdated);
    }

    @Test
    void deleteItem() {
        doNothing().when(itemRepository).deleteById(1L);
        itemService.deleteItem(expectedItem.getId());
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void addComments_whenValidComments_thenSaved() throws ValidationException, NotFoundException {
        Long userId = 2L;
        Long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.findAllByItemId(itemId)).thenReturn(List.of(expectedBooking));

        when(commentRepository.save(comment))
                .thenReturn(comment);
        CommentShort actualComment = itemService
                .addComments(userId, itemId, CommentMapper.commentDto(comment));

        assertEquals(CommentMapper.toCommentShort(comment), actualComment);
        verify(commentRepository).save(comment);
    }

    @Test
    void addComments_whenNotAuthorisedLeaveComments_thenThrownException() {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto expectedComment = new CommentDto(1L, "Best Item ever", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemService.addComments(expectedUser.getId(), expectedItem.getId(), expectedComment));
        assertEquals(validationException.getMessage(), "This user " + userId + " can't add comments.");
    }

    @Test
    void addComments_whenNotValidComments_thenThrownException() {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto expectedComment = new CommentDto(1L, "", null);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemService.addComments(userId, itemId, expectedComment));
        assertEquals(validationException.getMessage(), "Text is empty");
    }

    @Test
    void getItemDetailsWithNextAndLastBooking_WhenItemFound_thenReturned() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        ItemDetails testItem = new ItemDetails(expectedItem.getId(),
                expectedItem.getName(),
                expectedItem.getDescription(), expectedItem.getAvailable(),
                new BookingDetails(),
                new BookingDetails(),
                List.of(CommentMapper.toCommentShort(comment)));

        ItemDetails actualItem = itemService.getNextAndLastBookingsOfItem(itemId, userId);

        assertEquals(testItem.getId(), actualItem.getId());
    }

    @Test
    void getItemDetailsWithNextAndLastBooking_WhenItemNotFound_thenThrowException() {
        long ownerId = 2L;
        long itemId = 1L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.getNextAndLastBookingsOfItem(itemId, ownerId));
        assertEquals(notFoundException.getMessage(), "Such Item with id " + itemId + " doesn't exist.");
    }
}