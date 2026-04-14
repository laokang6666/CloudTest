package com.car.userservice.service;

import com.car.userservice.dto.CreateUserRequest;
import com.car.userservice.dto.UserResponse;

public interface UserAccountService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(Long id);
}
