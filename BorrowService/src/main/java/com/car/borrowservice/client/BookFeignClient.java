package com.car.borrowservice.client;

import com.car.borrowservice.config.MyCustomLoadBalancerConfiguration;
import com.car.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "book-service", contextId = "bookFeignClient", path = "/api/books")
public interface BookFeignClient {

    @PostMapping("/{id}/borrow")
    R borrowOne(@PathVariable("id") Long id, @RequestBody Map<String, Object> body);

    @PostMapping("/{id}/return")
    R returnOne(@PathVariable("id") Long id, @RequestBody Map<String, Object> body);
}
