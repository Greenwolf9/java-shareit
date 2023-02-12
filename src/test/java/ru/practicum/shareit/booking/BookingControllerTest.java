package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerShort;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.ItemForBookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private BookingInfoDto bookingInfoDto;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        bookingInfoDto = new BookingInfoDto(
                1L,
                LocalDateTime.parse("2023-02-08T13:35:20", formatter),
                LocalDateTime.parse("2023-02-09T13:35:20", formatter),
                new ItemForBookingInfoDto(1L, "Test ItemDto"),
                new BookerShort(2L, "MusterMann"),
                Status.WAITING);
    }

    @Test
    void saveBooking() throws Exception {
        when(bookingService.saveBooking(anyLong(), any()))
                .thenReturn(bookingInfoDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookingInfoDto.getBooker().getId())
                        .content(mapper.writeValueAsString(bookingInfoDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingInfoDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingInfoDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingInfoDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.status").value(bookingInfoDto.getStatus().toString()));
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingInfoDto);
        mvc.perform(patch("/bookings/{bookingId}", bookingInfoDto.getId())
                        .header("X-Sharer-User-Id", bookingInfoDto.getBooker().getId())
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingInfoDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingInfoDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingInfoDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingInfoDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.id").value(bookingInfoDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingInfoDto.getBooker().getName()))
                .andExpect(jsonPath("$.status").value(bookingInfoDto.getStatus().toString()));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingInfoDto);
        mvc.perform(get("/bookings/{bookingId}", bookingInfoDto.getId())
                        .header("X-Sharer-User-Id", bookingInfoDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingInfoDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingInfoDto.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingInfoDto.getEnd().format(formatter)))
                .andExpect(jsonPath("$.item.name").value(bookingInfoDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingInfoDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));

    }

    @Test
    void getBookingsByStateAndUserId() throws Exception {
        when(bookingService.getAllBookingsByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingInfoDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookingInfoDto.getBooker().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    void getBookingsByStateAndItemsOfOwner() throws Exception {
        when(bookingService.findAllByOwnerIdAndItem(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingInfoDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", bookingInfoDto.getBooker().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}