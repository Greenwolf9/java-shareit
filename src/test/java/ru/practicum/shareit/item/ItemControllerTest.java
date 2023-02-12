package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private ItemDto itemDto = new ItemDto(
            1L,
            "Test ItemDto",
            "Description of Test ItemDto",
            true,
            new User(2L, "newTest@test.ru", "MusterMann"),
            1L);

    private CommentShort commentShort = new CommentShort(1L, "Best Item ever", "MusterMann",
            LocalDateTime.parse("2023-02-09T13:35:20", formatter));

    private ItemDetails itemDetails = new ItemDetails(
            1L,
            "Test ItemDto",
            "Description of Test ItemDto",
            true,
            new BookingDetails(1L, 2L),
            new BookingDetails(2L, 2L),
            List.of(commentShort));

    @Test
    void addNewItem() throws Exception {
        when(itemService.saveItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", itemDto.getOwner().getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", itemDto.getOwner().getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void addNewComment() throws Exception {
        when(itemService.addComments(anyLong(), anyLong(), any()))
                .thenReturn(commentShort);
        mvc.perform(post("/items/{itemId}/comment", itemDetails.getId())
                        .header("X-Sharer-User-Id", itemDto.getOwner().getId())
                        .content(mapper.writeValueAsString(commentShort))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentShort.getId()))
                .andExpect(jsonPath("$.text").value(commentShort.getText()))
                .andExpect(jsonPath("$.authorName").value(commentShort.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentShort.getCreated().format(formatter)));
    }

    @Test
    void getItemDto() throws Exception {
        when(itemService.getNextAndLastBookingsOfItem(anyLong(), anyLong()))
                .thenReturn(itemDetails);
        mvc.perform(get("/items/{itemId}", itemDetails.getId())
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDetails.getId()))
                .andExpect(jsonPath("$.name").value(itemDetails.getName()))
                .andExpect(jsonPath("$.description").value(itemDetails.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDetails.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").value(itemDetails.getLastBooking()))
                .andExpect(jsonPath("$.nextBooking").value(itemDetails.getNextBooking()))
                .andExpect(jsonPath("$.comments", hasSize(itemDetails.getComments().size())))
                .andExpect(jsonPath("$.comments[0].id").value(1L))
                .andExpect(jsonPath("$.comments[0].text").value("Best Item ever"))
                .andExpect(jsonPath("$.comments[0].authorName").value("MusterMann"))
                .andExpect(jsonPath("$.comments[0].created").value(commentShort.getCreated().format(formatter)));
    }

    @Test
    void getAllItemOfUser() throws Exception {
        when(itemService.findListOfItemsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDetails));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchOfItems() throws Exception {
        String text = "est";
        when(itemService.getSearchedItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 2L)
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}