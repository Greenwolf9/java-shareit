package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemForBookingInfoDtoTest {
    @Autowired
    private JacksonTester<ItemForBookingInfoDto> json;

    @Test
    void testToItemForBookingInfoDto() throws Exception {
        ItemForBookingInfoDto itemForBookingInfoDto = new ItemForBookingInfoDto(1L, "Test ItemDto");

        JsonContent<ItemForBookingInfoDto> result = json.write(itemForBookingInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test ItemDto");

    }

}