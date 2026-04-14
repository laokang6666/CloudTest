package com.car.borrowservice.client;

import com.car.borrowservice.client.dto.UserRemoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", contextId = "userFeignClient", path = "/api/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserRemoteResponse getUser(@PathVariable("id") Long id);
}
