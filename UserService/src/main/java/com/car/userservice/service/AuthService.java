package com.car.userservice.service;

import com.car.userservice.dto.LoginRequest;
import com.car.userservice.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
