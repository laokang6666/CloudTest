package com.car.borrowservice.controller;

import com.car.borrowservice.client.BookFeignClient;
import com.car.borrowservice.client.UserFeignClient;
import com.car.borrowservice.client.dto.BookRemoteResponse;
import com.car.borrowservice.client.dto.UserRemoteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BorrowControllerApiTest {

    private static final LocalDateTime FIXED_AT = LocalDateTime.of(2026, 4, 1, 12, 0, 0);

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserFeignClient userFeignClient;

    @MockitoBean
    private BookFeignClient bookFeignClient;

    private static FeignException feignExceptionWithStatus(int httpStatus) {
        Request request = Request.create(
                Request.HttpMethod.POST,
                "http://localhost/test",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null);
        Response response = Response.builder()
                .status(httpStatus)
                .reason("test")
                .request(request)
                .build();
        return FeignException.errorStatus("testMethod", response);
    }

    @Test
    @DisplayName("POST /api/borrows/{id}/return 成功时返回 200 且 returnedAt 存在")
    void returnBorrow_ok() throws Exception {
        when(userFeignClient.getUser(10L)).thenReturn(new UserRemoteResponse(10L, "u1", "U1", FIXED_AT));
        when(bookFeignClient.borrowOne(eq(20L), anyMap()))
                .thenReturn(new BookRemoteResponse(20L, "Book", 5, 4, FIXED_AT));
        when(bookFeignClient.returnOne(eq(20L), anyMap()))
                .thenReturn(new BookRemoteResponse(20L, "Book", 5, 5, FIXED_AT));

        String created = mockMvc.perform(post("/api/borrows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10,\"bookId\":20}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long borrowId = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(post("/api/borrows/{id}/return", borrowId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(borrowId))
                .andExpect(jsonPath("$.bookId").value(20))
                .andExpect(jsonPath("$.returnedAt").exists());
    }

    @Test
    @DisplayName("重复还书返回 409 BORROW_ALREADY_RETURNED")
    void returnBorrow_twice_secondConflict() throws Exception {
        when(userFeignClient.getUser(11L)).thenReturn(new UserRemoteResponse(11L, "u2", "U2", FIXED_AT));
        when(bookFeignClient.borrowOne(eq(21L), anyMap()))
                .thenReturn(new BookRemoteResponse(21L, "B", 3, 2, FIXED_AT));
        when(bookFeignClient.returnOne(eq(21L), anyMap()))
                .thenReturn(new BookRemoteResponse(21L, "B", 3, 3, FIXED_AT));

        String created = mockMvc.perform(post("/api/borrows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":11,\"bookId\":21}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long borrowId = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(post("/api/borrows/{id}/return", borrowId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/borrows/{id}/return", borrowId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("BORROW_ALREADY_RETURNED"));
    }

    @Test
    @DisplayName("图书归还远程 409 映射为 STOCK_AT_CAPACITY")
    void returnBorrow_bookRemote409() throws Exception {
        when(userFeignClient.getUser(12L)).thenReturn(new UserRemoteResponse(12L, "u3", "U3", FIXED_AT));
        when(bookFeignClient.borrowOne(eq(22L), anyMap()))
                .thenReturn(new BookRemoteResponse(22L, "B2", 2, 1, FIXED_AT));
        when(bookFeignClient.returnOne(eq(22L), anyMap())).thenThrow(feignExceptionWithStatus(409));

        String created = mockMvc.perform(post("/api/borrows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":12,\"bookId\":22}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long borrowId = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(post("/api/borrows/{id}/return", borrowId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STOCK_AT_CAPACITY"));
    }
}
