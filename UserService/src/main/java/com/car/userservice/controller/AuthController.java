package com.car.userservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.car.common.api.R;
import com.car.userservice.dto.LoginRequest;
import com.car.userservice.sentinel.UserSentinelBlockHandler;
import com.car.userservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @SentinelResource(
            value = "authLogin",
            blockHandler = "blockLogin",
            blockHandlerClass = UserSentinelBlockHandler.class)
    public ResponseEntity<R> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(R.ok(authService.login(request)));
    }
}
