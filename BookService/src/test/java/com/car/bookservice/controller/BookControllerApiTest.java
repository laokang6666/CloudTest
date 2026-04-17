package com.car.bookservice.controller;

import com.car.bookservice.dto.CreateBookRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/books/{id}/return 借出后再还书可恢复可借册数")
    void borrowThenReturn_restoresAvailableStock() throws Exception {
        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("还书测试");
        req.setTotalStock(3);

        String created = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(created).get("body").get("id").asLong();

        mockMvc.perform(post("/api/books/{id}/borrow", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.availableStock").value(2));

        mockMvc.perform(post("/api/books/{id}/return", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.availableStock").value(3));
    }

    @Test
    @DisplayName("POST /api/books/{id}/return 可借已满时不能再还入 409 STOCK_AT_CAPACITY")
    void returnWhenAlreadyFull_returns409() throws Exception {
        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("满库存");
        req.setTotalStock(2);

        String created = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(created).get("body").get("id").asLong();

        mockMvc.perform(post("/api/books/{id}/return", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STOCK_AT_CAPACITY"));
    }

    @Test
    @DisplayName("借出后归还再归还第二次 409")
    void borrowReturnReturnAgain_returns409() throws Exception {
        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("一册");
        req.setTotalStock(1);

        String created = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(created).get("body").get("id").asLong();

        mockMvc.perform(post("/api/books/{id}/borrow", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/books/{id}/return", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.availableStock").value(1));

        mockMvc.perform(post("/api/books/{id}/return", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STOCK_AT_CAPACITY"));
    }
}
