package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInfoDtoTest {
    @Autowired
    private JacksonTester<BookingInfoDto> json;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testBookingInfoDtoTest() throws Exception {
        ItemForBookingInfoDto itemForBookingInfoDto = new ItemForBookingInfoDto(1L, "Test ItemDto");
        BookerShort bookerShort = new BookerShort(2L, "MusterMann");
        BookingInfoDto bookingInfoDto = new BookingInfoDto(
                1L,
                LocalDateTime.parse("2023-02-08T13:35:20", formatter),
                LocalDateTime.parse("2023-02-09T13:35:20", formatter),
                itemForBookingInfoDto,
                bookerShort,
                Status.WAITING);

        JsonContent<BookingInfoDto> result = json.write(bookingInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-02-08T13:35:20");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-02-09T13:35:20");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("MusterMann");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Test ItemDto");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

}