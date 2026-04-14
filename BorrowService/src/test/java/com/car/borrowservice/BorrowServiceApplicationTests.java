package com.car.borrowservice;

import com.car.borrowservice.client.BookFeignClient;
import com.car.borrowservice.client.UserFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BorrowServiceApplicationTests {

    @MockitoBean
    private UserFeignClient userFeignClient;

    @MockitoBean
    private BookFeignClient bookFeignClient;

    @Test
    void contextLoads() {
    }

}
