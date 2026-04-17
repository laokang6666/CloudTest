package com.car.userservice.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.car.common.api.R;
import com.car.userservice.dto.CreateUserRequest;
import com.car.userservice.dto.LoginRequest;
import com.car.userservice.dto.LoginResponse;
import com.car.userservice.dto.UserResponse;
import org.springframework.http.ResponseEntity;

/**
 * Sentinel 流控：返回统一 {@link R}，body 为空 DTO。
 */
public final class UserSentinelBlockHandler {

    public static final String CODE_FLOW_BLOCKED = "FLOW_BLOCKED";

    private UserSentinelBlockHandler() {}

    public static ResponseEntity<R> blockLogin(LoginRequest request, BlockException ex) {
        return ResponseEntity.ok(new R(CODE_FLOW_BLOCKED, "请求过于频繁", new LoginResponse(null, null)));
    }

    public static ResponseEntity<R> blockUserCreate(CreateUserRequest request, BlockException ex) {
        return ResponseEntity.ok(new R(CODE_FLOW_BLOCKED, "请求过于频繁", new UserResponse()));
    }

    public static ResponseEntity<R> blockUserGet(Long id, BlockException ex) {
        return ResponseEntity.ok(new R(CODE_FLOW_BLOCKED, "请求过于频繁", new UserResponse()));
    }
}
