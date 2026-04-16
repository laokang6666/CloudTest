package com.car.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Size(max = 64)
    private String loginName;

    @NotBlank
    @Size(max = 128)
    private String password;
}
