package com.car.userservice.dto;

/** 登录成功返回的 JWT 与类型（Bearer）。 */
public record LoginResponse(String token, String tokenType) {

    public static LoginResponse bearer(String token) {
        return new LoginResponse(token, "Bearer");
    }
}
