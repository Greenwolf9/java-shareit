package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureMockMvc
class ShareItTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void main() {
        ShareItApp.main(new String[]{});
    }

    @Test
    public void shouldReplyOk_getUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReplyServerError_getBookings() throws Exception {
        mvc.perform(get("/bookings"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldReplyNotFound_getRequests() throws Exception {
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
