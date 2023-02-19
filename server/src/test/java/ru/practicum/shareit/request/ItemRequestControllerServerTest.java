package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemDetailsForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDetails;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestControllerServer.class)
class ItemRequestControllerServerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ItemRequestDetails requestDetails = new ItemRequestDetails(1L,
            "Test description",
            LocalDateTime.parse(LocalDateTime.now().format(formatter)),
            List.of(new ItemDetailsForRequest(
                    1L,
                    "Test Name",
                    "Item Test Description",
                    true,
                    2L)));

    @Test
    void addNewRequest() throws Exception {
        when(itemRequestService.saveItemRequest(anyLong(), any()))
                .thenReturn(requestDetails);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDetails))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDetails.getId()))
                .andExpect(jsonPath("$.description").value(requestDetails.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDetails.getCreated().format(formatter)))
                .andExpect(jsonPath("$.items", hasSize(requestDetails.getItems().size())))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("Test Name"))
                .andExpect(jsonPath("$.items[0].description").value("Item Test Description"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].requestId").value(2L));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllItemRequestsByUserId(anyLong()))
                .thenReturn(List.of(requestDetails));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(requestDetails.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDetails.getDescription()))
                .andExpect(jsonPath("$[0].created").value(requestDetails.getCreated().format(formatter)))
                .andExpect(jsonPath("$[0].items", hasSize(requestDetails.getItems().size())))
                .andExpect(jsonPath("$[0].items[0].id").value(1L))
                .andExpect(jsonPath("$[0].items[0].name").value("Test Name"))
                .andExpect(jsonPath("$[0].items[0].description").value("Item Test Description"))
                .andExpect(jsonPath("$[0].items[0].available").value(true))
                .andExpect(jsonPath("$[0].items[0].requestId").value(2L));
    }

    @Test
    void getCurrentRequest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(requestDetails);
        mvc.perform(get("/requests/{requestId}", requestDetails.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDetails.getId()))
                .andExpect(jsonPath("$.description").value(requestDetails.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDetails.getCreated().format(formatter)))
                .andExpect(jsonPath("$.items", hasSize(requestDetails.getItems().size())))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("Test Name"))
                .andExpect(jsonPath("$.items[0].description").value("Item Test Description"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].requestId").value(2L));
    }

    @Test
    void getAllRequestsSorted() throws Exception {
        when(itemRequestService.getAllRequestsSorted(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDetails));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}