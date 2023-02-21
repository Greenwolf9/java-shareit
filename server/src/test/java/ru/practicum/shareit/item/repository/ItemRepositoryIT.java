package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemDetailsForRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryIT {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User user1;
    private Item item1;

    @BeforeEach
    void addItemsToRepository() {
        user1 = User.builder()
                .email("user1@test.com")
                .name("user1").build();
        userRepository.save(user1);
        item1 = Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user1)
                .build();
        itemRepository.save(item1);
    }

    @Test
    void findByItemIdAndUserId() {

        Pageable pageable = PageRequest.of(0, 20);
        Page<ItemView> itemViewPage = itemRepository.findByItemIdAndUserId(user1.getId(), pageable);
        assertThat(itemViewPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllByRequestId() {
        User requestor = User.builder().email("user2@test.com").name("user2").build();
        userRepository.save(requestor);
        ItemRequest request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("need best item ever")
                .requestor(requestor)
                .created(LocalDateTime.now()).build());
        item1.setRequest(request1);
        List<ItemDetailsForRequest> listOfRequests = itemRepository.findAllByRequestId(request1.getId());

        assertThat(listOfRequests).hasSize(1);

    }

    @Test
    void findItemByKeyWords() {
        String searchWords = "item";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Item> page = itemRepository.findItemByKeyWords(searchWords, pageable);
        assertThat(page).isNotEmpty().containsOnly(page.getContent().get(0));
    }

    @AfterEach
    void deleteRepositories() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

}