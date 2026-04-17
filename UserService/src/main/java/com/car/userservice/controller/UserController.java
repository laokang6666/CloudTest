package com.car.userservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.car.common.api.R;
import com.car.userservice.dto.CreateUserRequest;
import com.car.userservice.sentinel.UserSentinelBlockHandler;
import com.car.userservice.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAccountService userAccountService;

    public UserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    @SentinelResource(
            value = "userCreate",
            blockHandler = "blockUserCreate",
            blockHandlerClass = UserSentinelBlockHandler.class)
    public ResponseEntity<R> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(R.ok(userAccountService.createUser(request)));
    }

    @GetMapping("/{id}")
    @SentinelResource(
            value = "userGet",
            blockHandler = "blockUserGet",
            blockHandlerClass = UserSentinelBlockHandler.class)
    public ResponseEntity<R> get(@PathVariable Long id) {
        return ResponseEntity.ok(R.ok(userAccountService.getUser(id)));
    }
}
