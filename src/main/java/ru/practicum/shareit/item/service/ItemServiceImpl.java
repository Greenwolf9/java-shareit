package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto getItemById(Long itemId) throws NotFoundException {
        final Item item = itemRepository
                .findById(itemId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Such Item with id " + itemId + " doesn't exist."));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        final Collection<Item> items = itemRepository.findAll();
        return ItemMapper.toItemDtoList(items);
    }

    @Override
    public List<ItemDetails> getAllItemsByUserId(Long userId) {
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        return itemList.stream().map(item -> new ItemDetails(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                findLastBooking(bookingRepository.findAllByItemId(item.getId()), userId, item.getOwner().getId()),
                findNextBooking(bookingRepository.findAllByItemId(item.getId()), userId, item.getOwner().getId()),
                CommentMapper.toCommentShortList(commentRepository.findAllByItemId(item.getId()))))
                .collect(Collectors.toList());

    }

    @Override
    public ItemDto saveItem(Long userId, ItemDto itemDto) throws ValidationException, NotFoundException {
        final User user = userRepository.findById(userId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist"));
        final Item item = ItemMapper.toItem(itemDto);
        validateItem(item);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) throws NotFoundException {
        final Item itemToBeUpdated = itemRepository.findById(itemId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Such Item with id " + itemId + " doesn't exist."));
        if (itemToBeUpdated.getOwner().getId() != userId) {
            throw new NotFoundException("Wrong userId. Please check");
        }
        if (itemDto.getName() != null && !itemDto.getName().equals(itemToBeUpdated.getName())) {
            itemToBeUpdated.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(itemToBeUpdated.getDescription())) {
            itemToBeUpdated.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(itemToBeUpdated.getAvailable())) {
            itemToBeUpdated.setAvailable(itemDto.getAvailable());
        }
        final Item item = itemRepository.save(itemToBeUpdated);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDetails getNextAndLastBookingsOfItem(long itemId, long userId) throws NotFoundException{
        List<Booking> bookingList = bookingRepository.findAllByItemId(itemId);
        final Item item = itemRepository
                .findById(itemId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Such Item with id " + itemId + " doesn't exist."));
        return new ItemDetails(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                findLastBooking(bookingList, userId, item.getOwner().getId()),
                findNextBooking(bookingList, userId, item.getOwner().getId()),
                CommentMapper.toCommentShortList(commentRepository.findAllByItemId(item.getId())));
    }

    private BookingDetails findLastBooking(List<Booking> bookingList, long userId, long ownerId){
        if(userId != ownerId){
            return null;
        }
        return bookingList.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .map(n -> new BookingDetails(n.getId(), n.getBooker().getId())).orElse(null);
    }

    private BookingDetails findNextBooking(List<Booking> bookingList, long userId, long ownerId){
        if(userId != ownerId){
            return null;
        }
        return bookingList.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now())).min(Comparator.comparing(Booking::getStart))
                .map(n -> new BookingDetails(n.getId(), n.getBooker().getId())).orElse(null);
    }

    @Override
    public CommentShort addComments(Long userId, Long itemId, CommentDto commentDto) throws ValidationException, NotFoundException {
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new ValidationException("Text is empty");
        }
        final User user = userRepository.findById(userId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist"));

        final Item item = itemRepository.findById(itemId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Such Item with id " + itemId + " doesn't exist."));

        // проверяем, брал ли данный пользователь эту вещь в аренду, в т.ч. закончилась ли аренда
        if (bookingRepository.findAllByItemId(itemId)
                .stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .map(Booking::getBooker)
                .noneMatch(x -> x.getId().equals(userId))) {
            throw new ValidationException("This user " + userId + " can't add comments.");
        }
        final Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.toCommentShort(commentRepository.save(comment));
    }


    private void validateItem(Item item) throws ValidationException {
        if (item.getName().isEmpty() && item.getName().equals("")) {
            throw new ValidationException("Name is invalid. Please check.");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty() || item.getDescription().equals("")) {
            throw new ValidationException("Please add description.");
        }
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ValidationException("Please check availability.");
        }
    }
}
