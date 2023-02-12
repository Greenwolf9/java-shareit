package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemDetailsForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDetails;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository,
                                  UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ItemRequestDetails saveItemRequest(Long userId, ItemRequestDto itemRequestDto) throws ValidationException, NotFoundException {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " doesn't exist"));
        final ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto);
        validateDescriptionOfItemRequest(itemRequest);
        itemRequest.setRequestor(user);
        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    private void validateDescriptionOfItemRequest(ItemRequest itemRequest) throws ValidationException {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isEmpty()) {
            throw new ValidationException("Please validate description. Input is incorrect or empty.");
        }
    }

    @Override
    public ItemRequestDetails getItemRequestById(Long requestId, Long userId) throws NotFoundException {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("Request with id " + requestId + " doesn't exist.");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " doesn't exist");
        }
        final List<ItemDetailsForRequest> items = itemRepository.findAllByRequestId(requestId);
        ItemRequestDetails itemRequest = RequestMapper.toItemRequestDto(itemRequestRepository.getById(requestId));
        itemRequest.setItems(items);
        return itemRequest;
    }

    @Override
    public List<ItemRequestDetails> getAllItemRequestsByUserId(Long userId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Requestor is wrong");
        }
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(dto -> new ItemRequestDetails(dto.getId(),
                        dto.getDescription(),
                        dto.getCreated(),
                        validateList(dto.getItems())))
                .collect(Collectors.toList());
    }

    private List<ItemDetailsForRequest> validateList(List<ItemDetailsForRequest> forRequestList) {
        for (ItemDetailsForRequest i : forRequestList) {
            if (i.getId() == null) {
                return Collections.emptyList();
            }
        }
        return forRequestList;
    }

    @Override
    public List<ItemRequestDetails> getAllRequestsSorted(Long userId, int from, int size) throws ValidationException {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Input invalid. Check parameters.");
        }
        int p = from / size;
        return itemRequestRepository.findAllRequestsByRequestorIdIsNot(userId, PageRequest.of(p, size, Sort.by("created").descending())).stream()
                .map(dto -> new ItemRequestDetails(dto.getId(),
                        dto.getDescription(),
                        dto.getCreated(),
                        validateList(dto.getItems())))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemRequest(Long requestId) {
        itemRequestRepository.deleteById(requestId);
    }
}
