package com.car.userservice.service.impl;

import com.car.userservice.dto.CreateUserRequest;
import com.car.userservice.dto.UserResponse;
import com.car.userservice.entity.User;
import com.car.userservice.exception.DomainException;
import com.car.userservice.repository.UserRepository;
import com.car.userservice.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;

    public UserAccountServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String login = request.getLoginName().trim();
        if (userRepository.existsByLoginName(login)) {
            throw new DomainException(HttpStatus.CONFLICT, "DUPLICATE_LOGIN_NAME", "登录名已存在");
        }
        User user = new User();
        user.setLoginName(login);
        String display = request.getDisplayName();
        user.setDisplayName(display == null || display.isBlank() ? login : display.trim());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new DomainException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getLoginName(), user.getDisplayName(), user.getCreatedAt());
    }
}
