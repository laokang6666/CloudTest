package com.car.borrowservice.client;

import com.car.borrowservice.client.dto.BookRemoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "book-service", contextId = "bookFeignClient", path = "/api/books")
public interface BookFeignClient {

    @PostMapping("/{id}/borrow")
    BookRemoteResponse borrowOne(@PathVariable("id") Long id, @RequestBody Map<String, Object> body);
}
